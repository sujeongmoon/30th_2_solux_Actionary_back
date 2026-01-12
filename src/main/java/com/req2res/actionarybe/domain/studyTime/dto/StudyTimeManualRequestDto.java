package com.req2res.actionarybe.domain.studyTime.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyTimeManualRequestDto {

	@Min(value = 60, message = "누적된 공부시간은 최소 1분 이상이어야 합니다.")
	@Schema(description = "누적된 공부시간(초)", example = "1838984")
	private int durationSecond;

	@NotNull(message = "기록할 날짜는 비어있을 수 없습니다.")
	@Schema(description = "기록할 날짜", example = "2025-12-26")
	private LocalDate date;

}
