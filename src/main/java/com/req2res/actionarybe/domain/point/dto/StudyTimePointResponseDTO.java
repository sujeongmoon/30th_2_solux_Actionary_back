package com.req2res.actionarybe.domain.point.dto;

import com.req2res.actionarybe.domain.point.entity.PointSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "공부시간 포인트 적립 응답 DTO")
public class StudyTimePointResponseDTO {

    @Schema(
            description = "사용자 고유 ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long userId;

    @Schema(
            description = "이번에 적립된 포인트 (공부시간(초) -> 시간으로 환산 후 × 10, 반올림 적용)",
            example = "13",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int earnedPoint;

    @Schema(
            description = "포인트 적립 출처",
            example = "STUDY_TIME",
            allowableValues = {
                    "STUDY_TIME",
                    "STUDY_PARTICIPATION",
                    "TODO_COMPLETION"
            },
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private PointSource source;

    @Schema(
            description = "금일 누적 공부 시간 (단위: 초)",
            example = "4500", // 01:15:00
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long todayStudySeconds;

    @Schema(
            description = "표시용 금일 누적 공부 시간 (HH:MM:SS)",
            example = "01:15:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String todayStudyTime;

    @Schema(
            description = "포인트 적립 후 총 포인트",
            example = "120",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int totalPoint;
}
