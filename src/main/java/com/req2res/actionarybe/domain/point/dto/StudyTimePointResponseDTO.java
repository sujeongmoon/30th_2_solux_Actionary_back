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
            example = "1"
    )
    private Long userId;

    @Schema(
            description = "이번에 적립된 포인트 (공부시간 × 10, 반올림 적용)",
            example = "100"
    )
    private int earnedPoint;

    @Schema(
            description = "포인트 적립 출처",
            example = "STUDY_TIME",
            allowableValues = {
                    "STUDY_TIME",
                    "STUDY_PARTICIPATION",
                    "TODO_COMPLETION"
            }
    )
    private PointSource source;

    @Schema(
            description = "금일 누적 공부 시간 (단위: 시간)",
            example = "2.5"
    )
    private Double todayStudyHours;

    @Schema(
            description = "포인트 적립 후 총 포인트",
            example = "1230"
    )
    private int totalPoint;
}