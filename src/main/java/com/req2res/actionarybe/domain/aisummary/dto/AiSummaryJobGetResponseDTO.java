package com.req2res.actionarybe.domain.aisummary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "AI 요약 작업 단건 조회 응답 DTO")
public class AiSummaryJobGetResponseDTO {

    @Schema(description = "요약 작업 ID", example = "sb_7f2c1a")
    private String jobId;

    @Schema(description = "작업 상태", example = "PENDING")
    private AiSummaryEnums.Status status;

    @Schema(description = "작업 큐 등록 시간 (ISO 8601)", example = "2025-11-05T07:35:10Z")
    private String queuedAt;

    @Schema(description = "생성된 요약 결과 (SUCCEEDED 상태일 때만 포함)")
    private String summary;

    @Schema(description = "오류 정보 (FAILED 상태일 때만 포함)")
    private ErrorDTO error;

    // ---------- 내부 Error DTO ----------
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "요약 작업 오류 정보 DTO")
    public static class ErrorDTO {

        @Schema(description = "오류 코드", example = "UNSUPPORTED_PDF")
        private String code;

        @Schema(description = "오류 상세 메시지", example = "암호화된 PDF는 처리할 수 없습니다.")
        private String message;
    }
}
