package com.req2res.actionarybe.domain.aisummary.service;

import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryJobGetResponseDTO;
import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryResponseDataDTO;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryJob;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryResult;
import com.req2res.actionarybe.domain.aisummary.repository.AiSummaryJobRepository;
import com.req2res.actionarybe.domain.aisummary.repository.AiSummaryRedisRepository;
import com.req2res.actionarybe.domain.aisummary.repository.AiSummaryResultRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AiSummaryQueryService {

    private final AiSummaryRedisRepository aiSummaryRedisRepository;
    private final AiSummaryJobRepository aiSummaryJobRepository;
    private final AiSummaryResultRepository aiSummaryResultRepository;

    // 1. 요약 작업 단건 조회 API (상태/결과 확인)
    public AiSummaryJobGetResponseDTO getSummaryJob(String jobId) {

        // 1) Redis 먼저 조회
        Optional<AiSummaryResponseDataDTO> cached = aiSummaryRedisRepository.findJob(jobId);
        if (cached.isPresent()) {
            return mapFromRedisDTO(cached.get());
        }

        // 2) Redis에 없으면 DB 조회
        AiSummaryJob job = aiSummaryJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 jobId 입니다."));

        // 3) SUCCEEDED라면 result 테이블에서 summary 조회
        String summary = null;
        if (job.getStatus() == AiSummaryEnums.Status.SUCCEEDED) {
            summary = aiSummaryResultRepository.findByJobId(jobId)
                    .map(AiSummaryResult::getSummary)
                    .orElse(null);
        }

        // 4) DB 기반 응답 DTO로 변환
        return AiSummaryJobGetResponseDTO.builder()
                .jobId(job.getJobId())
                .status(job.getStatus())
                .queuedAt(job.getQueuedAt() == null ? null : job.getQueuedAt().toString())
                .summary(summary)
                .error(job.getStatus() == AiSummaryEnums.Status.FAILED
                        ? AiSummaryJobGetResponseDTO.ErrorDTO.builder()
                        .code(job.getErrorCode())
                        .message(job.getErrorMessage())
                        .build()
                        : null)
                .build();
    }

    private AiSummaryJobGetResponseDTO mapFromRedisDTO(AiSummaryResponseDataDTO redisData) {
        return AiSummaryJobGetResponseDTO.builder()
                .jobId(redisData.getJobId())
                .status(redisData.getStatus())
                .queuedAt(redisData.getQueuedAt())
                .summary(redisData.getSummary())
                .error(redisData.getError() == null ? null :
                        AiSummaryJobGetResponseDTO.ErrorDTO.builder()
                                .code(redisData.getError().getCode())
                                .message(redisData.getError().getMessage())
                                .build()
                )
                .build();
    }

    public String resolveMessage(AiSummaryEnums.Status status) {
        return switch (status) {
            case PENDING -> "요약 작업이 대기 중입니다.";
            case RUNNING -> "요약 작업이 진행 중입니다.";
            case SUCCEEDED -> "요약 작업이 완료되었습니다.";
            case FAILED -> "요약 작업이 실패했습니다.";
        };
    }
}



