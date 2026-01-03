package com.req2res.actionarybe.domain.point.dto;
//투두 완료 포인트 적립 API에서 사용하는 response DTO

import com.req2res.actionarybe.domain.point.entity.PointSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "투두 완료 포인트 적립 응답 DTO")
public class TodoCompletionPointResponseDTO {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "완료한 투두 ID", example = "55")
    private Long todoId;

    @Schema(description = "이번에 적립된 포인트 (1P 또는 한도 초과 시 0P)", example = "1")
    private int earnedPoint;

    @Schema(description = "포인트 적립 출처", example = "TODO_COMPLETION")
    private PointSource source;

    @Schema(description = "오늘까지 적립된 투두 포인트 개수", example = "3")
    private int todayTodoPointCount;

    @Schema(description = "하루 투두 포인트 제한(기본 5)", example = "5")
    private int todayTodoPointLimit;

    @Schema(description = "적립 후 총 포인트", example = "1241")
    private int totalPoint;
}
