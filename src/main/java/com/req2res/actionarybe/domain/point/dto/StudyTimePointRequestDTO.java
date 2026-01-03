package com.req2res.actionarybe.domain.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "공부시간 포인트 적립 요청 DTO")
public class StudyTimePointRequestDTO {

    @Schema(
            description = "이번에 기록한 공부 시간 (단위: 초)",
            example = "5400", // 1시간 30분
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "studySeconds는 필수입니다.")
    @Positive(message = "studySeconds는 0보다 커야 합니다.")
    private Long studySeconds;
}
