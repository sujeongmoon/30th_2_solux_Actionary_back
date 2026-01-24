package com.req2res.actionarybe.domain.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(
        description = "사이드바용 포인트 조회 응답 DTO",
        example = """
        {
          "userId": 7,
          "totalPoint": 210
        }
        """
)
public class MyPointSummaryResponseDTO {

    @Schema(description = "사용자 ID", example = "7")
    private Long userId;

    @Schema(description = "누적 포인트 합계", example = "210")
    private int totalPoint;
}
