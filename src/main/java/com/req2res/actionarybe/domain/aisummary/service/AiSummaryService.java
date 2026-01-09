package com.req2res.actionarybe.domain.aisummary.service;

import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryResponseDataDTO;
import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryUrlRequestDTO;
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
        int outTokens = (maxTokens == null) ? 300 : maxTokens;

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

        // PDF pageCount 기준 필요하면 여기서 계산(소형 판별용)
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
                        .status(AiSummaryResponseDataDTO.Status.SUCCEEDED)
                        .summary(summary)
                        .build();

            } catch (UnsupportedPdfException e) {
                job.markFailed("UNSUPPORTED_PDF", e.getMessage());
                jobRepo.save(job);

                return AiSummaryResponseDataDTO.builder()
                        .status(AiSummaryResponseDataDTO.Status.FAILED)
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
                        .status(AiSummaryResponseDataDTO.Status.FAILED)
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
                        .status(AiSummaryResponseDataDTO.Status.FAILED)
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
                .status(AiSummaryResponseDataDTO.Status.PENDING)
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
        String jobId = newJobId();
        String lang = (req.getLanguage() == null || req.getLanguage().isBlank()) ? "ko" : req.getLanguage();
        int outTokens = (req.getMaxTokens() == null) ? 300 : req.getMaxTokens();

        try {
            AiSummaryJob job = AiSummaryJob.builder()
                    .userId(userIdOrNull)
                    .jobId(jobId)
                    .sourceType(SourceType.URL)
                    .title(buildTitleFromUrl(req.getSourceUrl()))
                    .sourceUrl(req.getSourceUrl())
                    .language(lang)
                    .status(Status.RUNNING)
                    .hasFullSummary(false)
                    .build();
            jobRepo.save(job);

            // URL 다운로드 → PDF 텍스트 추출 → 요약
            byte[] bytes = downloadUrlAsBytes(req.getSourceUrl());

            if (!looksLikePdf(bytes)) {
                throw new UnsupportedPdfException("URL이 PDF를 반환하지 않습니다. (HTML/기타 파일일 수 있음)");
            }
            
            String text = extractPdfTextFromBytes(bytes);
            String summary = callOpenAi(text, lang, outTokens);

            resultRepo.save(AiSummaryResult.builder()
                    .jobId(jobId)
                    .summary(summary)
                    .build());

            job.markSucceeded(true);
            jobRepo.save(job);

            return AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryResponseDataDTO.Status.SUCCEEDED)
                    .summary(summary)
                    .build();

        } catch (UnsupportedPdfException e) {
            jobRepo.findByJobId(jobId).ifPresent(j -> {
                j.markFailed("UNSUPPORTED_PDF", e.getMessage());
                jobRepo.save(j);
            });

            return AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryResponseDataDTO.Status.FAILED)
                    .error(AiSummaryResponseDataDTO.AiError.builder()
                            .code("UNSUPPORTED_PDF")
                            .message(e.getMessage())
                            .build())
                    .build();

        } catch (Exception e) {
            jobRepo.findByJobId(jobId).ifPresent(j -> {
                j.markFailed("INTERNAL_ERROR", "요약 처리 중 오류가 발생했습니다.");
                jobRepo.save(j);
            });

            return AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryResponseDataDTO.Status.FAILED)
                    .error(AiSummaryResponseDataDTO.AiError.builder()
                            .code("INTERNAL_ERROR")
                            .message("요약 처리 중 오류가 발생했습니다.")
                            .build())
                    .build();
        }
    }


    // ===== helpers =====

    private String newJobId() {
        return "sb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private boolean isPdf(MultipartFile file) {
        String ct = file.getContentType();
        return ct != null && ct.toLowerCase().contains("pdf");
    }

    private String buildTitleFromFile(MultipartFile file) {
        return (file.getOriginalFilename() != null) ? file.getOriginalFilename() : "업로드 파일 요약";
    }

    private String buildTitleFromUrl(String url) {
        return (url.length() > 60) ? url.substring(0, 60) + "..." : url;
    }

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

    private String extractPdfTextFromS3(String key) {
        try (InputStream is = s3StorageService.download(key); PDDocument doc = PDDocument.load(is)) {
            if (doc.isEncrypted()) throw new UnsupportedPdfException("암호화된 PDF는 처리할 수 없습니다.");

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);

            // 토큰 입력 제한: 대충 길이 제한(정교 토큰 계산은 다음 단계)
            int maxChars = 6000;
            if (text.length() > maxChars) text = text.substring(0, maxChars);
            return text;

        } catch (UnsupportedPdfException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedPdfException("PDF 텍스트 추출에 실패했습니다.");
        }
    }

    @SuppressWarnings("unchecked")
    private String extractSummaryText(Map<String, Object> res) {

        // 1) 가장 쉬운 케이스: output_text
        Object outputText = res.get("output_text");
        if (outputText instanceof String s && !s.isBlank()) return s.trim();

        // 2) output -> content -> text
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

                        // 혹시 { text: { value: "..." } } 형태일 수도 있어서 대비
                        if (text instanceof Map<?, ?> tm) {
                            Object value = tm.get("value");
                            if (value instanceof String s2 && !s2.isBlank()) return s2.trim();
                        }
                    }
                }

                // 3) 혹시 item 자체에 text가 있는 구조 대비
                Object text = m.get("text");
                if (text instanceof String s && !s.isBlank()) return s.trim();
            }
        }

        // 4) 마지막 fallback: 전체 JSON을 로그로 보고 구조 확인
        return null;
    }


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

        if (summary == null || summary.isBlank()) {
            log.error("OpenAI response has no extractable text. res={}", res);
            throw new RuntimeException("OpenAI 응답에서 요약 텍스트를 찾지 못했습니다.");
        }

        return summary;


    }

    private static class UnsupportedPdfException extends RuntimeException {
        UnsupportedPdfException(String msg) { super(msg); }
    }

    public String summarizeFileFromS3Key(String s3Key, String language, int outTokens) {
        String text = extractPdfTextFromS3(s3Key);
        return callOpenAi(text, language, outTokens);
    }

    //URL → PDF bytes 다운로드 메서드
    private byte[] downloadUrlAsBytes(String url) {
        try {
            return WebClient.builder()
                    .build()
                    .get()
                    .uri(url)
                    // 일부 사이트는 User-Agent 없으면 403
                    .header("User-Agent", "Mozilla/5.0")
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("URL에서 파일 다운로드 실패: " + e.getMessage(), e);
        }
    }

    //PDF bytes → 텍스트 추출 메서드
    private String extractPdfTextFromBytes(byte[] pdfBytes) {
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            if (doc.isEncrypted()) throw new UnsupportedPdfException("암호화된 PDF는 처리할 수 없습니다.");

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);

            // 너무 길면 자르기 (임시)
            int maxChars = 12000;
            if (text.length() > maxChars) text = text.substring(0, maxChars);

            return text;

        } catch (UnsupportedPdfException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedPdfException("PDF 텍스트 추출에 실패했습니다.");
        }
    }

    //pdf인지 확인하는 메소드
    private boolean looksLikePdf(byte[] bytes) {
        return bytes != null && bytes.length >= 4
                && bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F';
    }


}
