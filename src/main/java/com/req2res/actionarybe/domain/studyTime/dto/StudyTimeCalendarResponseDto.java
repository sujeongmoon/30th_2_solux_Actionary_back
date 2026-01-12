package com.req2res.actionarybe.domain.studyTime.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyTimeCalendarResponseDto {

	@Schema(description = "당월 공부량 누적 시간 목록", example = "[\n"
		+ "\t\t  {\n"
		+ "\t\t\t  \"date\" : \"2025-10-01\",\n"
		+ "\t\t\t  \"durationSeconds\" : 10000\n"
		+ "\t\t  },\n"
		+ "\t\t  {\n"
		+ "\t\t\t  \"date\" : \"2025-10-02\",\n"
		+ "\t\t\t  \"durationSeconds\" : 0\n"
		+ "\t\t  },\n"
		+ "\t\t  ...\n"
		+ "\t\t  {\n"
		+ "\t\t\t  \"date\" : \"2025-10-31\",\n"
		+ "\t\t\t  \"durationSeconds\" : 10310\n"
		+ "\t\t  }\t\t  \n"
		+ "\t  ]")
	List<MonthlyDurationSecondsDto> monthlyDurationSeconds;

}
