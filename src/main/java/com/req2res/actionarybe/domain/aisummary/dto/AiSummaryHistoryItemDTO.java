package com.req2res.actionarybe.domain.aisummary.dto;

import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryJob;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "AI 요약 작업 목록의 단일 항목 DTO")
public class AiSummaryHistoryItemDTO {

    @Schema(description = "외부 노출용 요약 작업 ID", example = "sb_7f2c1a")
    private String jobId;

    @Schema(
            description = "요약 작업 상태 (PENDING, RUNNING, SUCCEEDED, FAILED)",
            example = "SUCCEEDED"
    )
    private String status;

    @Schema(
            description = "요약 요청 원본 타입 (FILE 또는 URL)",
            example = "FILE"
    )
    private String sourceType;

    @Schema(
            description = "요약 대상 제목 (파일명 또는 URL 기반 제목)",
            example = "프로젝트 결과 보고서"
    )
    private String title;

    @Schema(
            description = "업로드한 파일명 (sourceType=FILE일 때만 존재)",
            example = "report.pdf",
            nullable = true
    )
    private String fileName;

    @Schema(
            description = "요약 대상 URL (sourceType=URL일 때만 존재)",
            example = "https://example.com/paper.pdf",
            nullable = true
    )
    private String sourceUrl;

    @Schema(
            description = "요약 요청 생성 시각",
            example = "2025-11-05T07:35:10"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = """
                요약 작업 완료 시각
                
                - SUCCEEDED / FAILED 상태일 때만 값이 존재
                - 내부적으로 updatedAt 값을 사용
                """,
            example = "2025-11-05T07:37:40",
            nullable = true
    )
    private LocalDateTime finishedAt;

    @Schema(
            description = "요약 결과 언어",
            example = "ko"
    )
    private String language;

    @Schema(
            description = "상세 조회 시 요약 전문 제공 여부",
            example = "true"
    )
    private boolean hasFullSummary;

    public static AiSummaryHistoryItemDTO from(AiSummaryJob job) {

        LocalDateTime finishedAt = null;
        if (job.getStatus() == AiSummaryEnums.Status.SUCCEEDED
                || job.getStatus() == AiSummaryEnums.Status.FAILED) {
            finishedAt = job.getUpdatedAt();
        }

        return AiSummaryHistoryItemDTO.builder()
                .jobId(job.getJobId())
                .status(job.getStatus().name())
                .sourceType(job.getSourceType().name())
                .title(job.getTitle())
                .fileName(job.getFileName())
                .sourceUrl(job.getSourceUrl())
                .createdAt(job.getCreatedAt())
                .finishedAt(finishedAt)
                .language(job.getLanguage())
                .hasFullSummary(job.isHasFullSummary())
                .build();
    }
}
