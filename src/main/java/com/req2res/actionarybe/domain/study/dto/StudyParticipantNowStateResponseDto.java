package com.req2res.actionarybe.domain.study.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyParticipantNowStateResponseDto {

	@Schema(description = "스터디 id", example = "1")
	private Long studyId;

	@Schema(description = "스터디 참여 id", example = "1")
	private Long studyParticipantId;

	@Schema(description = "미디어 상태를 변경한 유저 id", example = "1")
	private Long userId;

	@Schema(description = "유저 말풍선", example = "조금만 쉬고 올게요")
	private String nowState;

}
