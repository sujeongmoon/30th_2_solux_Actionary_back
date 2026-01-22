package com.req2res.actionarybe.domain.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "캘린더 월별 DONE 투두 날짜별 집계 DTO")
public class TodoCalendarDoneSummaryDTO {

    @Schema(
            description = "투두 완료(DONE) 날짜",
            example = "2026-01-21"
    )
    private LocalDate date;

    @Schema(
            description = "해당 날짜에 완료(DONE)된 투두 개수",
            example = "2"
    )
    private long doneCount;
}
