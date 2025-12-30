package com.req2res.actionarybe.domain.point.dto;
// 스터디 참여 포인트 적립 API에서 사용하는 response DTO

import com.req2res.actionarybe.domain.point.entity.PointSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "스터디 참여 포인트 적립 응답 DTO")
public class StudyParticipationPointResponseDTO {

    @Schema(
            description = "포인트를 적립한 사용자 ID",
            example = "1"
    )
    private Long userId;

    @Schema(
            description = "참여한 스터디 방 ID",
            example = "12"
    )
    private Long studyRoomId;

    @Schema(
            description = "이번 요청으로 적립된 포인트 (고정 10P)",
            example = "10"
    )
    private int earnedPoint;

    @Schema(
            description = "포인트 적립 출처",
            example = "STUDY_PARTICIPATION"
    )
    private PointSource source;

    @Schema(
            description = "포인트 적립 후 사용자의 총 포인트",
            example = "1240"
    )
    private int totalPoint;
}
