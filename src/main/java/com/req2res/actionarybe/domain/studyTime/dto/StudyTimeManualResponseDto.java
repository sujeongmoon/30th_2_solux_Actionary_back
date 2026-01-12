package com.req2res.actionarybe.domain.studyTime.dto;

import java.time.LocalDate;

import com.req2res.actionarybe.domain.studyTime.entity.StudyTimeManual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyTimeManualResponseDto {

	@Schema(description = "수동등록 공부량 id", example = "1")
	private Long studyTimeManualId;

	@Schema(description = "기록한 날짜", example = "2025-12-26")
	private LocalDate manualDate;

	@Schema(description = "누적된 공부시간(초)", example = "1838984")
	private int durationSecond;

	@Schema(description = "유저 id", example = "1")
	private Long userId;

	public static StudyTimeManualResponseDto from(StudyTimeManual studyTimeManual) {
		return StudyTimeManualResponseDto.builder()
			.studyTimeManualId(studyTimeManual.getId())
			.manualDate(studyTimeManual.getManualDate())
			.durationSecond(studyTimeManual.getDurationSecond())
			.userId(studyTimeManual.getUserId())
			.build();
	}

}
