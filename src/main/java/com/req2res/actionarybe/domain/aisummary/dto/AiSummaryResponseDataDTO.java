package com.req2res.actionarybe.domain.aisummary.dto;

import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "AI 요약 응답 데이터")
public class AiSummaryResponseDataDTO {

    @Schema(
            description = "요약 작업 상태",
            example = "SUCCEEDED",
            allowableValues = {"PENDING", "RUNNING", "SUCCEEDED", "FAILED"}
    )
    private AiSummaryEnums.Status status;

    @Schema(
            description = "요약 결과 텍스트 (SUCCEEDED 상태일 때만 존재)",
            example = "이 문서는 AI 요약 서비스의 전체 구조를 설명합니다."
    )
    private String summary;

    @Schema(
            description = "비동기 요약 작업 ID (202 응답 또는 조회 시 사용)",
            example = "job_20260108_abcdef"
    )
    private String jobId;

    @Schema(
            description = "요약 작업이 큐에 들어간 시각 (202 응답 시)",
            example = "2026-01-08T12:30:00Z"
    )
    private String queuedAt;

    @Schema(description = "요약 실패 시 에러 정보 (FAILED 상태일 때만 존재)")
    private AiError error;

    public enum Status {
        PENDING, RUNNING, SUCCEEDED, FAILED
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(description = "AI 요약 실패 에러 정보")
    public static class AiError {

        @Schema(
                description = "에러 코드",
                example = "UNSUPPORTED_PDF"
        )
        private String code;

        @Schema(
                description = "에러 상세 메시지",
                example = "해당 PDF 형식은 지원하지 않습니다."
        )
        private String message;
    }
}
