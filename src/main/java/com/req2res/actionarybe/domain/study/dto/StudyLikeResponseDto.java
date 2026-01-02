package com.req2res.actionarybe.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyLikeResponseDto {

	@Schema(description = "스터디 id", example = "1")
	private Long studyId;

	@JsonProperty("isLiked")
	@Schema(description = "스터디 즐겨찾기 여부", example = "true")
	private Boolean isLiked;
}
