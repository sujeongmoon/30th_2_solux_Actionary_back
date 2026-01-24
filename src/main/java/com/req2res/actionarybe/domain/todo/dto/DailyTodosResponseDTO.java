package com.req2res.actionarybe.domain.todo.dto;
// 특정 날짜 투두 목록 조회 API 응답 DTO

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "특정 날짜의 투두 목록 응답 DTO")
public class DailyTodosResponseDTO {

    // 조회한 날짜 (yyyy-MM-dd)
    @Schema(
            description = "조회 날짜",
            example = "2026-01-01"
    )
    private String date;

    // 해당 날짜의 투두 목록
    @Schema(
            description = "투두 리스트"
    )
    private List<TodoResponseDTO> todos;
}
