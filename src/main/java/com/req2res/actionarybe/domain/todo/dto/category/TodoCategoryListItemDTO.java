package com.req2res.actionarybe.domain.todo.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "투두 카테고리 목록 조회 아이템 DTO")
public class TodoCategoryListItemDTO {

    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "카테고리 이름", example = "공부")
    private String name;

    @Schema(description = "카테고리 색상 (HEX 코드)", example = "#D29AFA")
    private String color;

    @Schema(
            description = "카테고리가 적용되기 시작하는 날짜 (이 날짜부터 카테고리 노출)",
            example = "2026-01-20"
    )
    private LocalDate startDate;

    @Schema(description = "카테고리 생성 일시", example = "2025-12-30T13:45:00")
    private LocalDateTime createdAt;
}
