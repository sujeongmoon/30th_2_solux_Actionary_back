package com.req2res.actionarybe.domain.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "사용자 포인트 요약 정보")
public class PublicPointsDTO {

    @Schema(
            description = "공부시간 기록으로 적립된 포인트 합계",
            example = "100"
    )
    private int study;

    @Schema(
            description = "스터디 참여로 적립된 포인트 합계",
            example = "20"
    )
    private int studyParticipation;

    @Schema(
            description = "투두 완료로 적립된 포인트 합계",
            example = "2"
    )
    private int todo;

    @Schema(
            description = "전체 포인트 합계 (study + studyParticipation + todo)",
            example = "122"
    )
    private int total;
}
