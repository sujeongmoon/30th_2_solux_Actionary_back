package com.req2res.actionarybe.domain.study.dto;

import com.req2res.actionarybe.domain.study.entity.Study;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudySummaryDto {

	@Schema(description = "스터디 id", example = "1")
	private Long studyId;

	@Schema(description = "스터디 이름", example = "06-12 임용송이 오전 공부반")
	private String studyName;

	@Schema(description = "스터디 커버 사진", example = "image_url.jpg")
	private String coverImage;

	public static StudySummaryDto from(Study study) {
		return new StudySummaryDto(
			study.getId(),
			study.getName(),
			study.getCoverImage()
		);
	}
}
