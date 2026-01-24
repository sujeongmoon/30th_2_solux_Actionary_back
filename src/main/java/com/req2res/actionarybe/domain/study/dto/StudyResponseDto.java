package com.req2res.actionarybe.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.req2res.actionarybe.domain.study.entity.Category;
import com.req2res.actionarybe.domain.study.entity.Study;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyResponseDto {

	@Schema(description = "스터디 id", example = "1")
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

	@Schema(description = "스터디 안내", example = "숙명여대생 인증 받습니다! 눈송이들만 모여있는 방이니 다른 분들은 출입 자제해 주세요. 많관부 많관부 ~")
	private String longDescription;

	@Schema(description = "스터디 인원제한", example = "15")
	private int memberLimit;

	@JsonProperty("isPublic")
	@Schema(description = "스터디 공개 여부", example = "false")
	private Boolean isPublic;

	@Schema(description = "스터디 개설한 유저 id", example = "1")
	private Long creatorUserId;

	public static StudyResponseDto from(Study study) {
		return StudyResponseDto.builder()
			.studyId(study.getId())
			.studyName(study.getName())
			.coverImage(study.getCoverImage())
			.category(study.getCategory())
			.categoryLabel(study.getCategory().getLabel())
			.description(study.getDescription())
			.longDescription(study.getLongDescription())
			.memberLimit(study.getMemberLimit())
			.isPublic(study.getIsPublic())
			.creatorUserId(study.getCreator().getId())
			.build();
	}
}
