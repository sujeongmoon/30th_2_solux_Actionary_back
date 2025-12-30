package com.req2res.actionarybe.domain.todo.dto.category;
// 투두 카테고리 생성 API 응답 DTO

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "투두 카테고리 생성 응답 DTO")
public class TodoCategoryCreateResponseDTO {

    // 카테고리 고유 ID
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    // 카테고리 이름
    @Schema(description = "카테고리 이름", example = "공부")
    private String name;

    // 카테고리 색상 (HEX)
    @Schema(description = "카테고리 색상 (HEX 코드)", example = "#FF5733")
    private String color;

    // 카테고리 생성 시각
    @Schema(description = "카테고리 생성 일시", example = "2025-12-30T13:45:00")
    private LocalDateTime createdAt;
}
