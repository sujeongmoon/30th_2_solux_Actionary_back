package com.req2res.actionarybe.domain.study.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyParticipantPrivateRequestDto {

	@Pattern(regexp = "^[0-9]{6}$", message = "비밀번호는 숫자 6자리여야 합니다.")
	@Schema(description = "스터디 비밀번호", example = "060522")
	private String password;

}
