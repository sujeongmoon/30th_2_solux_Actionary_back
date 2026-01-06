package com.req2res.actionarybe.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "페이지 정보")
public class PageInfoDTO {

    @Schema(description = "현재 페이지 번호(1부터 시작)", example = "1")
    private int page;

    @Schema(description = "페이지당 항목 수", example = "10")
    private int size;

    @Schema(description = "전체 결과 개수", example = "1")
    private long totalElements;

    @Schema(description = "전체 페이지 수", example = "1")
    private int totalPages;
}
