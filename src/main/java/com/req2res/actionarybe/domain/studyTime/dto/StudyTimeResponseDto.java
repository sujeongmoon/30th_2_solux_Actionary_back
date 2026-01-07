package com.req2res.actionarybe.domain.studyTime.dto;

import com.req2res.actionarybe.domain.studyTime.entity.Type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyTimeResponseDto {

	@Schema(description = "공부량 id", example = "1")
	private Long studyTimeId;

	@Schema(description = "스터디 참여 id", example = "1")
	private Long studyParticipantId;

	@Schema(description = "스터디 id", example = "1")
	private Long studyId;

	@Schema(description = "현재 스터디에 참여한 사용자 id", example = "1")
	private Long userId;

	@Schema(description = "전환된 타이머 종류", example = "BREAK")
	private Type changedType;

	@Schema(description = "전환된 타이머 종류 라벨", example = "휴식 시간")
	private String changedTypeLabel;

	@Schema(description = "해당 studyParticipant에서 누적된 공부시간(초)", example = "10000")
	private long totalStudySeconds;

	@Schema(description = "해당 studyParticipant에서 누적된 쉬는시간(초)", example = "8888")
	private long totalBreakSeconds;

}
