package com.req2res.actionarybe.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "통합 검색 결과 DTO")
public class IntegratedSearchResponseDTO {

    @Schema(description = "스터디 검색 결과 배열")
    private List<StudySearchResponseDTO> studies;

    @Schema(description = "게시글 검색 결과 배열")
    private List<PostSearchResponseDTO> posts;
}
