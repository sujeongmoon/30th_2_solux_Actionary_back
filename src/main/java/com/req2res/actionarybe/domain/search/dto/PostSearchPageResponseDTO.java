package com.req2res.actionarybe.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "게시글 검색 결과 페이지 응답 DTO")
public class PostSearchPageResponseDTO {

    @Schema(description = "게시글 검색 결과 목록")
    private List<PostSearchResponseDTO> content;

    @Schema(description = "페이지 정보")
    private PageInfoDTO pageInfo;
}
