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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums.SourceType;
import static com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums.Status;

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
            } catch (Exception e) {
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
                .queuedAt(java.time.Instant.now())
                .build();

        redisRepo.saveJob(jobId, pending, JOB_TTL);
        redisRepo.enqueue(jobId);

        return pending; // Controller에서 202로 감싸면 됨
    }

    public AiSummaryResponseDataDTO summarizeUrl(AiSummaryUrlRequestDTO req, Long userIdOrNull) {
        String jobId = newJobId();
        String lang = (req.getLanguage() == null || req.getLanguage().isBlank()) ? "ko" : req.getLanguage();
        int outTokens = (req.getMaxTokens() == null) ? 300 : req.getMaxTokens();

        // URL은 일단 동기 처리(스펙상 비동기로 돌릴 수도 있지만 처음엔 단순하게)
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

            String summary = callOpenAi("다음 URL 내용을 요약해줘: " + req.getSourceUrl(), lang, outTokens);

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

    public AiSummaryResponseDataDTO getJobStatus(String jobId) {
        // Redis 우선
        var cached = redisRepo.findJob(jobId);
        if (cached.isPresent()) return cached.get();

        // Redis TTL 만료 시: DB 기반으로 반환
        AiSummaryJob job = jobRepo.findByJobId(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 jobId 입니다."));

        if (job.getStatus() == Status.SUCCEEDED) {
            String summary = resultRepo.findByJobId(jobId)
                    .map(AiSummaryResult::getSummary)
                    .orElse(null);

            return AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryResponseDataDTO.Status.SUCCEEDED)
                    .summary(summary)
                    .build();
        }

        if (job.getStatus() == Status.FAILED) {
            return AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryResponseDataDTO.Status.FAILED)
                    .error(AiSummaryResponseDataDTO.AiError.builder()
                            .code(job.getErrorCode())
                            .message(job.getErrorMessage())
                            .build())
                    .build();
        }

        // PENDING/RUNNING
        return AiSummaryResponseDataDTO.builder()
                .status(AiSummaryResponseDataDTO.Status.valueOf(job.getStatus().name()))
                .jobId(jobId)
                .build();
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

    private String callOpenAi(String inputText, String language, int maxOutputTokens) {
        Map<String, Object> body = Map.of(
                "model", model,
                "instructions", "너는 문서를 요약하는 도우미야. 출력 언어는 " + language + "로. 최대 300토큰 내로 요약해.",
                "max_output_tokens", maxOutputTokens,
                "input", inputText
        );

        Map<?, ?> res = openAiWebClient.post()
                .uri("/responses")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (res == null) throw new RuntimeException("OpenAI 응답이 비었습니다.");

        Object outputText = res.get("output_text");
        if (outputText instanceof String s && !s.isBlank()) return s;

        // fallback
        return res.toString();
    }

    private static class UnsupportedPdfException extends RuntimeException {
        UnsupportedPdfException(String msg) { super(msg); }
    }

    public String summarizeFileFromS3Key(String s3Key, String language, int outTokens) {
        String text = extractPdfTextFromS3(s3Key);
        return callOpenAi(text, language, outTokens);
    }

}
