package com.req2res.actionarybe.domain.study.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingDurationDto {

	@Schema(description = "유저 id", example = "1")
	private Long userId;

	@Schema(description = "스터디 참여 유저 이름", example = "눈송이")
	private String userNickname;

	@Schema(description = "유저의 해당 스터디 공부량 누적 시간 (초)", example = "10800")
	private Long durationSeconds;
}
