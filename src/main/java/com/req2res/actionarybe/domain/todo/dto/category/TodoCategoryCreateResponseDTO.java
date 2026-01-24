package com.req2res.actionarybe.domain.todo.dto.category;
// 투두 카테고리 생성 API 응답 DTO

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "투두 카테고리 생성 응답 DTO")
public class TodoCategoryCreateResponseDTO {

    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "카테고리 이름", example = "공부")
    private String name;

    @Schema(description = "카테고리 색상 (HEX 코드),색상은 \\\\\\\"#D29AFA\\\\\\\", \\\\\\\"#6BEBFF\\\\\\\", \" +\n" +
            "                    \"\\\\\\\"#9AFF5B\\\\\\\", \\\\\\\"#FFAD36\\\\\\\",\\\\\\\"#FF8355\\\\\\\", \\\\\\\"#FCDF2F\\\\\\\", \\\\\\\"#FF3D2F\\\\\\\", \\\\\\\"#FF9E97\\\\\\\"중에만 가능합니다.\"", example = "#D29AFA")
    private String color;

    @Schema(description = "카테고리 적용 시작 날짜", example = "2026-01-20")
    private LocalDate startDate;

    @Schema(description = "카테고리 생성 일시", example = "2025-12-30T13:45:00")
    private LocalDateTime createdAt;
}
