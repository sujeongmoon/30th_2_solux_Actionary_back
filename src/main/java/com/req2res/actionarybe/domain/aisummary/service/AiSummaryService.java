package com.req2res.actionarybe.domain.aisummary.service;

import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryResponseDataDTO;
import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryUrlRequestDTO;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryJob;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryResult;
import com.req2res.actionarybe.domain.aisummary.repository.AiSummaryJobRepository;
import com.req2res.actionarybe.domain.aisummary.repository.AiSummaryRedisRepository;
import com.req2res.actionarybe.domain.aisummary.repository.AiSummaryResultRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums.SourceType;
import static com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums.Status;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSummaryService {

    private final WebClient openAiWebClient;
    private final S3StorageService s3StorageService;

    private final AiSummaryJobRepository jobRepo;
    private final AiSummaryResultRepository resultRepo;
    private final AiSummaryRedisRepository redisRepo;

    private final WebClient downloadClient;

    @Value("${openai.model}")
    private String model;

    private static final long QUICK_MAX_BYTES = 20L * 1024 * 1024;
    private static final int QUICK_MAX_PAGES = 30;
    private static final Duration JOB_TTL = Duration.ofHours(6);

    // 1. 파일 요약 API
    public AiSummaryResponseDataDTO summarizeFile(MultipartFile file, String language, Integer maxTokens, Long userIdOrNull) {
        // PDF만 허용 (png/jpg 등은 400)
        if (!isPdf(file)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "PDF 파일만 업로드할 수 있습니다.");
        }


        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "file은 필수입니다.");
        }

        String jobId = newJobId();
        String lang = (language == null || language.isBlank()) ? "ko" : language;
        int outTokens = (maxTokens == null) ? 600 : maxTokens;

        // 1) S3 업로드 먼저 (워커가 읽어야 함)
        String s3Key = s3StorageService.upload(file);

        // 2) 메타(Job) 먼저 저장 (목록 조회 기반)
        AiSummaryJob job = AiSummaryJob.builder()
                .userId(userIdOrNull)
                .jobId(jobId)
                .sourceType(SourceType.FILE)
                .title(buildTitleFromFile(file))
                .fileName(file.getOriginalFilename())
                .filePath(s3Key) // S3 Key 저장
                .language(lang)
                .status(Status.PENDING)
                .hasFullSummary(false)
                .build();

        Integer pageCount = null;
        if (isPdf(file)) {
            pageCount = getPdfPageCountFromS3(s3Key);
        }

        boolean isQuick = (file.getSize() < QUICK_MAX_BYTES) && (pageCount == null || pageCount < QUICK_MAX_PAGES);

        if (isQuick) {
            // 동기 즉시 요약
            try {
                job.markRunning();
                jobRepo.save(job);

                String text = extractPdfTextFromS3(s3Key);
                String summary = callOpenAi(text, lang, outTokens);

                resultRepo.save(AiSummaryResult.builder()
                        .jobId(jobId)
                        .summary(summary)
                        .build());

                job.markSucceeded(true);
                jobRepo.save(job);

                return AiSummaryResponseDataDTO.builder()
                        .status(AiSummaryEnums.Status.SUCCEEDED)
                        .summary(summary)
                        .build();

            } catch (UnsupportedPdfException e) {
                job.markFailed("UNSUPPORTED_PDF", e.getMessage());
                jobRepo.save(job);

                return AiSummaryResponseDataDTO.builder()
                        .status(AiSummaryEnums.Status.FAILED)
                        .error(AiSummaryResponseDataDTO.AiError.builder()
                                .code("UNSUPPORTED_PDF")
                                .message(e.getMessage())
                                .build())
                        .build();
            } catch (WebClientResponseException.TooManyRequests e) {
                log.error("OpenAI error status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);

                log.warn("OpenAI rate limit exceeded. jobId={}", jobId);

                job.markFailed("RATE_LIMIT", "요약 요청이 많아 잠시 후 다시 시도해주세요.");
                jobRepo.save(job);

                return AiSummaryResponseDataDTO.builder()
                        .status(AiSummaryEnums.Status.FAILED)
                        .error(AiSummaryResponseDataDTO.AiError.builder()
                                .code("RATE_LIMIT")
                                .message("요약 요청이 많아 잠시 후 다시 시도해주세요.")
                                .build())
                        .build();
            }catch (Exception e) {
                log.error("AI summary failed. jobId={}, fileName={}", jobId, file.getOriginalFilename(), e);
                job.markFailed("INTERNAL_ERROR", "요약 처리 중 오류가 발생했습니다.");
                jobRepo.save(job);

                return AiSummaryResponseDataDTO.builder()
                        .status(AiSummaryEnums.Status.FAILED)
                        .error(AiSummaryResponseDataDTO.AiError.builder()
                                .code("INTERNAL_ERROR")
                                .message("요약 처리 중 오류가 발생했습니다.")
                                .build())
                        .build();
            }


        }

        // 3) 대형이면 비동기 큐 등록
        job.markQueued(LocalDateTime.now());
        jobRepo.save(job);

        AiSummaryResponseDataDTO pending = AiSummaryResponseDataDTO.builder()
                .status(AiSummaryEnums.Status.PENDING)
                .jobId(jobId)
                .queuedAt(
                        job.getQueuedAt() != null
                                ? job.getQueuedAt().toString()
                                : null
                )
                .build();

        redisRepo.saveJob(jobId, pending, JOB_TTL);
        redisRepo.enqueue(jobId);

        return pending;
    }

    // 2. URL 파일 요약 API
    public AiSummaryResponseDataDTO summarizeUrl(AiSummaryUrlRequestDTO req, Long userIdOrNull) {
        if (req == null || req.getSourceUrl() == null || req.getSourceUrl().isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "sourceUrl은 필수입니다.");
        }

        String jobId = newJobId();
        String lang = (req.getLanguage() == null || req.getLanguage().isBlank()) ? "ko" : req.getLanguage();
        int outTokens = (req.getMaxTokens() == null) ? 600 : req.getMaxTokens();

        // 1) Job 먼저 저장
        AiSummaryJob job = AiSummaryJob.builder()
                .userId(userIdOrNull)
                .jobId(jobId)
                .sourceType(SourceType.URL)
                .title(buildTitleFromUrl(req.getSourceUrl()))
                .sourceUrl(req.getSourceUrl())
                .language(lang)
                .status(Status.PENDING)
                .hasFullSummary(false)
                .build();
        jobRepo.save(job);

        try {
            // 2) URL 다운로드 (동기 기본)
            job.markRunning();
            jobRepo.save(job);

            byte[] bytes = downloadUrlAsBytes(req.getSourceUrl());

            // 2-1) 절대 용량 제한 (안전장치)
            if (bytes.length > QUICK_MAX_BYTES) {
                return enqueueUrlJob(job, jobId, "파일이 커서 비동기로 처리합니다.");
            }

            // 2-2) PDF 검증
            validatePdfOrThrow(bytes);

            // 2-3) 페이지 수 확인
            int pageCount = getPdfPageCountFromBytes(bytes);

            boolean isQuick = (bytes.length < QUICK_MAX_BYTES) && (pageCount < QUICK_MAX_PAGES);

            if (isQuick) {
                // 즉시 요약 (동기)
                String text = extractPdfTextFromBytes(bytes);
                String summary = callOpenAi(text, lang, outTokens);

                resultRepo.save(AiSummaryResult.builder()
                        .jobId(jobId)
                        .summary(summary)
                        .build());

                job.markSucceeded(true);
                jobRepo.save(job);

                return AiSummaryResponseDataDTO.builder()
                        .status(AiSummaryEnums.Status.SUCCEEDED)
                        .jobId(jobId)
                        .summary(summary)
                        .build();
            }

            // 큰 PDF면 비동기(파일요약과 동일)
            return enqueueUrlJob(job, jobId, "페이지 수가 많아 비동기로 처리합니다.");

        } catch (UnsupportedPdfException e) {
            job.markFailed("UNSUPPORTED_PDF", e.getMessage());
            jobRepo.save(job);

            return AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryEnums.Status.FAILED)
                    .jobId(jobId)
                    .error(AiSummaryResponseDataDTO.AiError.builder()
                            .code("UNSUPPORTED_PDF")
                            .message(e.getMessage())
                            .build())
                    .build();

        } catch (WebClientResponseException.TooManyRequests e) {
            // OpenAI rate limit
            log.error("OpenAI error status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);

            job.markFailed("RATE_LIMIT", "요약 요청이 많아 잠시 후 다시 시도해주세요.");
            jobRepo.save(job);

            return AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryEnums.Status.FAILED)
                    .jobId(jobId)
                    .error(AiSummaryResponseDataDTO.AiError.builder()
                            .code("RATE_LIMIT")
                            .message("요약 요청이 많아 잠시 후 다시 시도해주세요.")
                            .build())
                    .build();

        } catch (Exception e) {
            log.error("summarizeUrl failed. jobId={}, url={}", jobId, req.getSourceUrl(), e);

            job.markFailed("INTERNAL_ERROR", "요약 처리 중 오류가 발생했습니다.");
            jobRepo.save(job);

            return AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryEnums.Status.FAILED)
                    .jobId(jobId)
                    .error(AiSummaryResponseDataDTO.AiError.builder()
                            .code("INTERNAL_ERROR")
                            .message("요약 처리 중 오류가 발생했습니다.")
                            .build())
                    .build();
        }
    }

    private AiSummaryResponseDataDTO enqueueUrlJob(AiSummaryJob job, String jobId, String reason) {
        // 파일요약과 동일하게 QUEUED 처리
        job.markQueued(LocalDateTime.now());
        jobRepo.save(job);

        AiSummaryResponseDataDTO pending = AiSummaryResponseDataDTO.builder()
                .status(AiSummaryEnums.Status.PENDING)
                .jobId(jobId)
                .queuedAt(job.getQueuedAt() != null ? job.getQueuedAt().toString() : null)
                .build();

        redisRepo.saveJob(jobId, pending, JOB_TTL);
        redisRepo.enqueue(jobId);

        return pending;
    }

    private int getPdfPageCountFromBytes(byte[] pdfBytes) {
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            if (doc.isEncrypted()) throw new UnsupportedPdfException("암호화된 PDF는 처리할 수 없습니다.");
            return doc.getNumberOfPages();
        } catch (UnsupportedPdfException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedPdfException("PDF를 읽을 수 없습니다. (손상/암호화 가능)");
        }
    }

    // ===== helpers =====

    // 요약 작업을 식별하기 위한 새로운 jobId 생성 (sb_ + UUID 앞 8자리)
    private String newJobId() {
        return "sb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    // 업로드된 파일이 PDF인지 Content-Type 기준으로 확인
    private boolean isPdf(MultipartFile file) {
        String ct = file.getContentType();
        return ct != null && ct.toLowerCase().contains("pdf");
    }

    // 파일 업로드 시 파일명을 기반으로 요약 제목 생성
    private String buildTitleFromFile(MultipartFile file) {
        return (file.getOriginalFilename() != null) ? file.getOriginalFilename() : "업로드 파일 요약";
    }

    // URL 요약 시 URL 문자열을 기반으로 요약 제목 생성 (너무 길면 잘라냄)
    private String buildTitleFromUrl(String url) {
        return (url.length() > 60) ? url.substring(0, 60) + "..." : url;
    }

    // S3에 저장된 PDF를 열어 전체 페이지 수를 계산
    private Integer getPdfPageCountFromS3(String key) {
        try (InputStream is = s3StorageService.download(key); PDDocument doc = PDDocument.load(is)) {
            if (doc.isEncrypted()) throw new UnsupportedPdfException("암호화된 PDF는 처리할 수 없습니다.");
            return doc.getNumberOfPages();
        } catch (UnsupportedPdfException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedPdfException("PDF를 읽을 수 없습니다. (손상/암호화 가능)");
        }
    }

    // S3에 저장된 PDF에서 텍스트를 추출 (최대 길이 제한 포함)
    private String extractPdfTextFromS3(String key) {
        try (InputStream is = s3StorageService.download(key); PDDocument doc = PDDocument.load(is)) {
            if (doc.isEncrypted()) throw new UnsupportedPdfException("암호화된 PDF는 처리할 수 없습니다.");

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);

            // OpenAI 입력 제한 대비 텍스트 길이 제한
            int maxChars = 6000;
            if (text.length() > maxChars) text = text.substring(0, maxChars);
            return text;

        } catch (UnsupportedPdfException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedPdfException("PDF 텍스트 추출에 실패했습니다.");
        }
    }

    // OpenAI API를 호출하여 텍스트 요약 요청 (incomplete/추출실패 시 1회 재시도 포함)
    private String callOpenAi(String inputText, String language, int maxOutputTokens) {
        if (inputText == null || inputText.isBlank()) {
            throw new RuntimeException("요약할 입력 텍스트가 비어있습니다.");
        }

        String lang = (language == null || language.isBlank()) ? "ko" : language;

        // 600 유지하되, "목표"는 낮춰서 끊김 방지
        int targetTokens = Math.max(200, Math.min(maxOutputTokens, maxOutputTokens - 100));

        Map<String, Object> body = Map.of(
                "model", model,
                "instructions", buildInstructions(lang, targetTokens),
                "input", inputText,
                "max_output_tokens", maxOutputTokens,
                // reasoning 토큰을 줄여서 실제 text가 나오게
                "reasoning", Map.of("effort", "minimal"),
                // 안전한 text 설정만
                "text", Map.of("format", Map.of("type", "text"))
        );

        Map<String, Object> res;
        try {
            log.info("OpenAI request start. model={}, lang={}, maxOutputTokens={}, inputChars={}",
                    model, lang, maxOutputTokens, inputText.length());

            res = openAiWebClient.post()
                    .uri("/responses")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

        } catch (WebClientResponseException.BadRequest e) {
            log.error("OpenAI 400 BadRequest. body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (WebClientResponseException.TooManyRequests e) {
            log.error("OpenAI 429 TooManyRequests. body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (WebClientResponseException e) {
            log.error("OpenAI error status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        }

        String summary = extractSummaryText(res);
        if (summary != null && !summary.isBlank() && !looksTruncated(summary)) {
            return summary.trim();
        }


        // 1회 재시도: 더 짧게 + reasoning 더 강하게 줄이기
        if (shouldRetry(res)) {
            int retryTarget = Math.max(120, targetTokens / 2);

            Map<String, Object> retryBody = Map.of(
                    "model", model,
                    "instructions", buildRetryInstructions(lang, retryTarget),
                    "input", inputText,
                    "max_output_tokens", maxOutputTokens,
                    "reasoning", Map.of("effort", "minimal"),
                    "text", Map.of("format", Map.of("type", "text"))
            );

            try {
                log.info("OpenAI retry start. model={}, retryTargetTokens={}, maxOutputTokens={}, inputChars={}",
                        model, retryTarget, maxOutputTokens, inputText.length());

                Map<String, Object> retryRes = openAiWebClient.post()
                        .uri("/responses")
                        .bodyValue(retryBody)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();

                String retrySummary = extractSummaryText(retryRes);
                if (retrySummary != null && !retrySummary.isBlank() && !looksTruncated(retrySummary)) {
                    return retrySummary.trim();
                }

                log.error("OpenAI retry response still has no extractable text. res={}", retryRes);

            } catch (WebClientResponseException e) {
                log.error("OpenAI retry error status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
                throw e;
            }
        }

        log.error("OpenAI response has no extractable text. res={}", res);
        throw new RuntimeException("OpenAI 응답에서 요약 텍스트를 찾지 못했습니다.");
    }

    // instructions 생성
    private String buildInstructions(String language, int targetTokens) {
        return """
        너는 문서를 요약하는 도우미야.
        출력 언어는 %s.
        반드시 요약 텍스트만 출력해.

        규칙:
        - 전체 8~12문장 이내
        - 문장이 중간에 끊기면 안 됨. 길면 핵심만 남기고 과감히 생략해.
        """.formatted(language);
    }


    // 재시도용 instructions (더 짧게 강제)
    private String buildRetryInstructions(String language, int retryTargetTokens) {
        return """
        너는 문서를 요약하는 도우미야.
        출력 언어는 %s.
        반드시 요약 텍스트만 출력해.

        규칙:
        - 전체 5~7문장 이내
        - 중간에 끊기면 안 됨. 중요한 내용만 남겨.
        """.formatted(language);
    }

    // 재시도 필요 조건 판단: incomplete(max_output_tokens) 또는 텍스트 추출 실패
    @SuppressWarnings("unchecked")
    private boolean shouldRetry(Map<String, Object> res) {
        if (res == null) return false;

        Object status = res.get("status");
        if ("incomplete".equals(status)) {
            Object details = res.get("incomplete_details");
            if (details instanceof Map<?, ?> m) {
                Object reason = m.get("reason");
                if ("max_output_tokens".equals(reason)) return true;
            }
            // incomplete 자체면 재시도 가치 있음
            return true;
        }

        // output이 reasoning만 오는 경우도 재시도 대상 (텍스트가 없다는 의미)
        Object output = res.get("output");
        if (output instanceof java.util.List<?> list && !list.isEmpty()) {
            boolean hasAnyText = list.stream()
                    .filter(o -> o instanceof Map<?, ?>)
                    .map(o -> (Map<?, ?>) o)
                    .anyMatch(this::containsAnyText);
            return !hasAnyText;
        }

        return false;
    }

    private boolean containsAnyText(Map<?, ?> item) {
        Object content = item.get("content");
        if (content instanceof java.util.List<?> cList) {
            for (Object c : cList) {
                if (!(c instanceof Map<?, ?> cm)) continue;
                Object text = cm.get("text");
                if (text instanceof String s && !s.isBlank()) return true;
                if (text instanceof Map<?, ?> tm) {
                    Object value = tm.get("value");
                    if (value instanceof String s2 && !s2.isBlank()) return true;
                }
            }
        }
        Object text = item.get("text");
        return (text instanceof String s && !s.isBlank());
    }

    // OpenAI Responses API 응답에서 요약 텍스트만 안전하게 추출
    @SuppressWarnings("unchecked")
    private String extractSummaryText(Map<String, Object> res) {
        if (res == null) return null;

        // 1) output_text 필드가 바로 있는 경우
        Object outputText = res.get("output_text");
        if (outputText instanceof String s && !s.isBlank()) return s.trim();

        // 2) Responses API 정석: output[].content[].type=output_text, text=...
        Object output = res.get("output");
        if (output instanceof java.util.List<?> outList) {
            StringBuilder sb = new StringBuilder();

            for (Object item : outList) {
                if (!(item instanceof Map<?, ?> m)) continue;

                // output item이 message가 아닐 수도 있음 (reasoning 등)
                Object content = m.get("content");
                if (!(content instanceof java.util.List<?> cList)) continue;

                for (Object c : cList) {
                    if (!(c instanceof Map<?, ?> cm)) continue;

                    // content.type 확인 (output_text 우선)
                    Object type = cm.get("type");
                    Object text = cm.get("text");

                    String extracted = null;

                    if (text instanceof String s && !s.isBlank()) extracted = s.trim();
                    else if (text instanceof Map<?, ?> tm) {
                        Object value = tm.get("value");
                        if (value instanceof String s2 && !s2.isBlank()) extracted = s2.trim();
                    }

                    if (extracted != null) {
                        // type이 없거나(output_text 아닐 수도) 텍스트가 있으면 일단 누적
                        // (실제 응답 구조 변형에도 최대한 안전하게)
                        if (sb.length() > 0) sb.append("\n");
                        sb.append(extracted);
                    }
                }
            }

            String joined = sb.toString().trim();
            if (!joined.isBlank()) return joined;
        }

        // 3) item 자체에 text가 있는 구조 대비 (fallback)
        if (output instanceof java.util.List<?> outList2) {
            for (Object item : outList2) {
                if (!(item instanceof Map<?, ?> m)) continue;
                Object text = m.get("text");
                if (text instanceof String s && !s.isBlank()) return s.trim();
            }
        }

        return null;
    }


    // PDF 처리 중 발생하는 예외를 표현하기 위한 커스텀 런타임 예외
    private static class UnsupportedPdfException extends RuntimeException {
        UnsupportedPdfException(String msg) { super(msg); }
    }

    // S3에 저장된 PDF를 요약하는 최종 진입 메소드
    public String summarizeFileFromS3Key(String s3Key, String language, int outTokens) {
        String text = extractPdfTextFromS3(s3Key);
        return callOpenAi(text, language, outTokens);
    }

    // URL로부터 파일을 다운로드하여 byte[] 형태로 반환
    private byte[] downloadUrlAsBytes(String url) {
        try {
            return downloadClient.get()
                    .uri(url)
                    .header("User-Agent", "Mozilla/5.0")
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("URL에서 파일 다운로드 실패: " + e.getMessage(), e);
        }
    }

    // PDF byte 배열로부터 텍스트를 추출
    private String extractPdfTextFromBytes(byte[] pdfBytes) {
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            if (doc.isEncrypted()) throw new UnsupportedPdfException("암호화된 PDF는 처리할 수 없습니다.");

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);

            // URL PDF는 상대적으로 길 수 있어 더 크게 제한
            int maxChars = 15000;
            if (text.length() > maxChars) text = text.substring(0, maxChars);

            return text;

        } catch (UnsupportedPdfException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedPdfException("PDF 텍스트 추출에 실패했습니다.");
        }
    }

    // 다운로드한 byte[]가 실제 PDF 파일인지 매직 넘버로 검증
    private void validatePdfOrThrow(byte[] bytes) {
        if (bytes == null || bytes.length < 4 ||
                bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D' || bytes[3] != 'F') {
            throw new UnsupportedPdfException("URL이 PDF를 반환하지 않습니다. (HTML/에러 페이지일 수 있음)");
        }
    }

    // 요약본 끊긴걸 감지하는 함수
    private boolean looksTruncated(String s) {
        if (s == null) return true;
        String t = s.trim();
        if (t.isEmpty()) return true;


        // 문장 종결로 끝나지 않으면 끊김 가능성이 큼
        char last = t.charAt(t.length() - 1);

        // 일반 문장부호
        if (last == '.' || last == '!' || last == '?' || last == '”' || last == '"' || last == '’' || last == '\'') {
            return false;
        }

        // 한국어 종결 어미로 끝나는지(완전 정확하진 않지만 실전에서 꽤 먹힘)
        return !(t.endsWith("다") || t.endsWith("요") || t.endsWith("니다") || t.endsWith("음"));
    }

}

