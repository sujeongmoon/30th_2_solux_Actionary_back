package com.req2res.actionarybe.domain.point.dto;
// 공부시간 포인트 적립 API에서 사용하는 request DTO

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
            description = "이번에 기록한 공부 시간 (단위: 시간)",
            example = "1.5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "studyHours는 필수입니다.")
    @Positive(message = "studyHours는 0보다 커야 합니다.")
    private Double studyHours;
}