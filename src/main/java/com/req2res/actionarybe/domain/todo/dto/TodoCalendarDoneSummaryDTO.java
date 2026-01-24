package com.req2res.actionarybe.domain.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "캘린더 월별 투두 날짜별 집계 DTO (DONE + TOTAL)")
public class TodoCalendarDoneSummaryDTO {

    @Schema(
            description = "투두 날짜",
            example = "2026-01-21"
    )
    private LocalDate date;

    @Schema(
            description = "해당 날짜에 완료(DONE)된 투두 개수",
            example = "2"
    )
    private long doneCount;

    @Schema(
            description = "해당 날짜의 전체 투두 개수 (상태 무관, DONE 포함)",
            example = "5"
    )
    private long totalTodoCount;
}
