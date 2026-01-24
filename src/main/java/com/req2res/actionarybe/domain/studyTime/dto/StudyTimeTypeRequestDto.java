package com.req2res.actionarybe.domain.studyTime.dto;

import com.req2res.actionarybe.domain.studyTime.entity.Type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyTimeTypeRequestDto {

	@Schema(description = "공부/휴식시간", example = "STUDY")
	private Type type;

}
