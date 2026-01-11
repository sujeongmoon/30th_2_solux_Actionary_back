package com.req2res.actionarybe.domain.studyTime.dto;

import java.time.LocalDate;

import com.req2res.actionarybe.domain.studyTime.entity.Period;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyTimePeriodResponseDto {

	@Schema(description = "스터디 공부량 기간 범위", example = "DAY")
	private Period period;

	@Schema(description = "사용자가 입력한 date", example = "사용자가 입력한 date")
	private LocalDate date;

	@Schema(description = "당일 공부량 누적 시간(초)", example = "10800")
	private Long durationSeconds;

}
