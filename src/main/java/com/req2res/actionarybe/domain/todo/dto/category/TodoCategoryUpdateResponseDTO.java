package com.req2res.actionarybe.domain.todo.dto.category;
// 투두 카테고리 수정 API에서 사용하는 응답 DTO

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "투두 카테고리 수정 응답 DTO")
public class TodoCategoryUpdateResponseDTO {

    // 카테고리 고유 ID
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    // 수정된 카테고리 이름
    @Schema(description = "카테고리 이름", example = "운동")
    private String name;

    // 수정된 카테고리 색상 (HEX)
    @Schema(description = "카테고리 색상 (HEX 코드)", example = "#2196F3")
    private String color;
}
