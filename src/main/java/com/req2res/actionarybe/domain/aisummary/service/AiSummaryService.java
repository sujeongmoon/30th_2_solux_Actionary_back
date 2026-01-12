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

    // OpenAI Responses API 응답에서 요약 텍스트만 안전하게 추출
    @SuppressWarnings("unchecked")
    private String extractSummaryText(Map<String, Object> res) {

        // 1) output_text 필드가 바로 있는 경우
        Object outputText = res.get("output_text");
        if (outputText instanceof String s && !s.isBlank()) return s.trim();

        // 2) output -> content -> text 구조 탐색
        Object output = res.get("output");
        if (output instanceof java.util.List<?> outList) {
            for (Object item : outList) {
                if (!(item instanceof Map<?, ?> m)) continue;

                Object content = m.get("content");
                if (content instanceof java.util.List<?> cList) {
                    for (Object c : cList) {
                        if (!(c instanceof Map<?, ?> cm)) continue;

                        Object text = cm.get("text");
                        if (text instanceof String s && !s.isBlank()) return s.trim();

                        // { text: { value: "..." } } 형태 대비
                        if (text instanceof Map<?, ?> tm) {
                            Object value = tm.get("value");
                            if (value instanceof String s2 && !s2.isBlank()) return s2.trim();
                        }
                    }
                }

                // 3) item 자체에 text가 있는 구조 대비
                Object text = m.get("text");
                if (text instanceof String s && !s.isBlank()) return s.trim();
            }
        }

        // 추출 실패 시 null 반환 (호출부에서 에러 처리)
        return null;
    }

    // OpenAI API를 호출하여 텍스트 요약 요청
    private String callOpenAi(String inputText, String language, int maxOutputTokens) {
        Map<String, Object> body = Map.of(
                "model", model,
                "instructions", "너는 문서를 요약하는 도우미야. 출력 언어는 " + language
                        + "로. 요약만 출력해. 최대 " + maxOutputTokens + " 토큰 이내.",
                "input", inputText,
                "max_output_tokens", maxOutputTokens,
                "text", Map.of(
                        "format", Map.of("type", "text")
                )
        );

        Map<String, Object> res = openAiWebClient.post()
                .uri("/responses")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        String summary = extractSummaryText(res);

        // 요약 텍스트가 없으면 에러 처리
        if (summary == null || summary.isBlank()) {
            log.error("OpenAI response has no extractable text. res={}", res);
            throw new RuntimeException("OpenAI 응답에서 요약 텍스트를 찾지 못했습니다.");
        }

        return summary;
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


}
