package com.req2res.actionarybe.domain.study.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RankingBoardDto {

	@Schema(description = "유저 id", example = "1")
	private Long userId;

	@Schema(description = "스터디 참여 유저 이름", example = "눈송이")
	private String userNickname;

	@Schema(description = "유저의 해당 스터디 당일 공부량 누적 시간 (초)", example = "10800")
	private Long todayDurationSeconds;

	@Schema(description = "유저의 해당 스터디 전체 공부량 누적 시간 (초)", example = "46800")
	private Long totalDurationSeconds;

}
