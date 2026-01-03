package com.req2res.actionarybe.domain.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "공개 프로필 포인트 조회 응답 DTO")
public class PublicUserPointResponseDTO {

    @Schema(
            description = "사용자 ID",
            example = "7",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long userId;

    @Schema(
            description = "사용자 닉네임",
            example = "다현",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nickname;

    @Schema(
            description = "포인트 요약 정보 (공부 / 스터디 / 투두 / 총합)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private PublicPointsDTO points;

    @Schema(
            description = "보유 배지 목록 (없을 경우 빈 배열)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<PublicBadgeDTO> badges;
}
