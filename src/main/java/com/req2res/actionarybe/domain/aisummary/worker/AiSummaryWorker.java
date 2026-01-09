package com.req2res.actionarybe.domain.aisummary.worker;

import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryResponseDataDTO;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryJob;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryResult;
import com.req2res.actionarybe.domain.aisummary.repository.AiSummaryJobRepository;
import com.req2res.actionarybe.domain.aisummary.repository.AiSummaryRedisRepository;
import com.req2res.actionarybe.domain.aisummary.repository.AiSummaryResultRepository;
import com.req2res.actionarybe.domain.aisummary.service.AiSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
@RequiredArgsConstructor
public class AiSummaryWorker {

    private final AiSummaryRedisRepository redisRepo;
    private final AiSummaryJobRepository jobRepo;
    private final AiSummaryResultRepository resultRepo;
    private final AiSummaryService aiSummaryService;

    private static final Duration JOB_TTL = Duration.ofHours(6);

    @Scheduled(fixedDelay = 2000)
    public void work() {
        String jobId = redisRepo.popNextJobId();
        if (jobId == null) return;

        AiSummaryJob job = jobRepo.findByJobId(jobId).orElse(null);
        if (job == null) return;

        // RUNNING 반영 (DB + Redis)
        job.markRunning();
        jobRepo.save(job);

        AiSummaryResponseDataDTO running = AiSummaryResponseDataDTO.builder()
                .status(AiSummaryResponseDataDTO.Status.RUNNING)
                .jobId(jobId)
                .build();
        redisRepo.saveJob(jobId, running, JOB_TTL);

        try {
            // 1) 실제 요약 실행 (여기서 OpenAI + S3 사용)
            String summary = aiSummaryService.summarizeFileFromS3Key(
                    job.getFilePath(),   // S3 key
                    job.getLanguage(),   // 요약 언어
                    600
            );

            // 2) 결과 DB 저장
            resultRepo.save(
                    AiSummaryResult.builder()
                            .jobId(jobId)
                            .summary(summary)
                            .build()
            );

            // 3) Job 상태 성공 처리
            job.markSucceeded(true);
            jobRepo.save(job);

            // 4) Redis에 SUCCEEDED + 짧은 summary 저장
            AiSummaryResponseDataDTO done = AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryResponseDataDTO.Status.SUCCEEDED)
                    .jobId(jobId)
                    .summary(
                            summary.length() > 600
                                    ? summary.substring(0, 600)
                                    : summary
                    )
                    .build();

            redisRepo.saveJob(jobId, done, JOB_TTL);

        } catch (Exception e) {
            job.markFailed("INTERNAL_ERROR", "요약 처리 중 오류가 발생했습니다.");
            jobRepo.save(job);

            AiSummaryResponseDataDTO failed = AiSummaryResponseDataDTO.builder()
                    .status(AiSummaryResponseDataDTO.Status.FAILED)
                    .jobId(jobId)
                    .error(AiSummaryResponseDataDTO.AiError.builder()
                            .code("INTERNAL_ERROR")
                            .message("요약 처리 중 오류가 발생했습니다.")
                            .build())
                    .build();

            redisRepo.saveJob(jobId, failed, JOB_TTL);
        }

    }
}
