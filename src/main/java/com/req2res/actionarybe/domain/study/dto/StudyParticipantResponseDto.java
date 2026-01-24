package com.req2res.actionarybe.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.req2res.actionarybe.domain.study.entity.StudyParticipant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyParticipantResponseDto {

	@Schema(description = "스터디 참여 id", example = "1")
	private Long studyParticipantId;

	@Schema(description = "스터디 id", example = "1")
	private Long studyId;

	@JsonProperty("isActive")
	@Schema(description = "현재 스터디 접속 여부", example = "false")
	private Boolean isActive;

	@Schema(description = "현재 스터디에 참여한 사용자 id", example = "1")
	private Long userId;

	public static StudyParticipantResponseDto from(StudyParticipant studyParticipant) {
		return StudyParticipantResponseDto.builder()
			.studyParticipantId(studyParticipant.getId())
			.studyId(studyParticipant.getStudy().getId())
			.isActive(studyParticipant.getIsActive())
			.userId(studyParticipant.getMember().getId())
			.build();
	}

}
