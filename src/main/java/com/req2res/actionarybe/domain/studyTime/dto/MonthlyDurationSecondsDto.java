package com.req2res.actionarybe.domain.studyTime.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MonthlyDurationSecondsDto {

	@Schema(description = "공부 조회 날짜", example = "2025-10-01")
	LocalDate date;

	@Schema(description = "당일 공부량 누적 시간(초)", example = "10000")
	long durationSeconds;
}
