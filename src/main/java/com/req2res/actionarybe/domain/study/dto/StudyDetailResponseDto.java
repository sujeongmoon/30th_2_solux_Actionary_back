package com.req2res.actionarybe.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.req2res.actionarybe.domain.study.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyDetailResponseDto {

	@Schema(description = "스터디 id", example = "2")
	private Long studyId;

	@Schema(description = "스터디 이름", example = "06-12 임용송이 오전 공부반")
	private String studyName;

	@Schema(description = "스터디 커버 사진", example = "image_url.jpg")
	private String coverImage;

	@Schema(description = "스터디 카테고리", example = "TEACHER_EXAM")
	private Category category;

	@Schema(description = "스터디 카테고리 라벨명", example = "임용")
	private String categoryLabel;

	@Schema(description = "스터디 간단 소개글", example = "눈송이 인증 받아요")
	private String description;

	@Schema(description = "스터디에 접속 중인 현재 인원", example = "5")
	private int memberNow;

	@Schema(description = "스터디 인원제한", example = "15")
	private int memberLimit;

	@JsonProperty("isPublic")
	@Schema(description = "스터디 공개 여부", example = "false")
	private Boolean isPublic;

	@JsonProperty("isStudyLike")
	@Schema(description = "로그인한 유저의 해당 스터디 좋아요 여부", example = "true")
	private Boolean isStudyLike;

	@JsonProperty("isStudyOwner")
	@Schema(description = "스터디 개설 유저 여부", example = "true")
	private Boolean isStudyOwner;
}
