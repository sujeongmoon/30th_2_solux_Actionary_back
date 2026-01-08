package com.req2res.actionarybe.domain.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "사용자가 보유한 배지 정보")
public class PublicBadgeDTO {

    @Schema(
            description = "배지 ID",
            example = "1"
    )
    private Long badgeId;

    @Schema(
            description = "배지 이름",
            example = "첫 공부 달성"
    )
    private String name;
}
