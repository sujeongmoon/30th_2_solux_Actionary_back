package com.req2res.actionarybe.domain.aisummary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "AI 요약 목록 조회 응답 DTO")
public class AiSummaryListResponseDTO {

    @Schema(description = "요약 작업 목록")
    private List<AiSummaryHistoryItemDTO> content;

    @Schema(
            description = "현재 페이지 번호 (1-base)",
            example = "1"
    )
    private int page;

    @Schema(
            description = "페이지 크기",
            example = "10"
    )
    private int size;

    @Schema(
            description = "전체 요약 작업 개수",
            example = "23"
    )
    private long totalElements;

    @Schema(
            description = "전체 페이지 수",
            example = "3"
    )
    private int totalPages;
}
