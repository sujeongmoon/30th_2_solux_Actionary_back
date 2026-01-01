package com.req2res.actionarybe.domain.study.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HitStudySummaryDto {
	@Schema(description = "스터디 id", example = "1")
	private Long studyId;

	@Schema(description = "스터디 이름", example = "06-12 임용송이 오전 공부반")
	private String studyName;

	@Schema(description = "스터디 커버 사진", example = "image_url.jpg")
	private String coverImage;

	@Schema(description = "스터디 간단 소개글", example = "눈송이 인증 받아요")
	private String description;

	@Schema(description = "스터디에 접속 중인 현재 인원", example = "5")
	private long memberNow;
}
