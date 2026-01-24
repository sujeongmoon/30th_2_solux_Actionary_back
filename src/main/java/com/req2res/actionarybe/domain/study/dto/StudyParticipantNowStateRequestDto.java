package com.req2res.actionarybe.domain.study.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyParticipantNowStateRequestDto {

	@Size(max = 20, message = "유저 말풍선은 20자 이내여야 합니다.")
	@Schema(description = "유저 말풍선", example = "조금만 쉬고 올게요")
	private String nowState;

}
