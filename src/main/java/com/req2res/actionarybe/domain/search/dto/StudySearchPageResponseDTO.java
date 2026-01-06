package com.req2res.actionarybe.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "스터디 검색 결과 페이지 응답 데이터")
public class StudySearchPageResponseDTO {

    @Schema(description = "검색 결과 리스트")
    private List<StudySearchResponseDTO> content;

    @Schema(description = "페이지 정보")
    private PageInfoDTO pageInfo;
}
