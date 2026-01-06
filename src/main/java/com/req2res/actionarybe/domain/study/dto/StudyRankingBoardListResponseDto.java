package com.req2res.actionarybe.domain.study.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyRankingBoardListResponseDto {

	@Schema(description = "스터디 id", example = "3")
	private Long studyId;

	@JsonProperty("isToday")
	@Schema(description = "스터디 일간/전체 기준 여부", example = "true")
	private Boolean isToday;

	@Schema(description = "스터디 랭킹 보드 목록", example = " [\n"
		+ "      {\n"
		+ "        \"userId\": 1,\n"
		+ "        \"userNickname\": \"눈송이\",\n"
		+ "        \"todayDurationSeconds\": 10800,\n"
		+ "        \"totalDurationSeconds\": 46800\n"
		+ "      },\n"
		+ "      {\n"
		+ "        \"userId\": 2,\n"
		+ "        \"userNickname\": \"쬬르디\",\n"
		+ "        \"todayDurationSeconds\": 5400,\n"
		+ "        \"totalDurationSeconds\": 52800\n"
		+ "      }\n"
		+ "    ]")
	private List<RankingBoardDto> rankingBoards;

}
