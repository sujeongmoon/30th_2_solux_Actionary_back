package com.req2res.actionarybe.domain.todo.dto;
// 투두 수정 API 요청 DTO

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투두 수정 요청 DTO")
public class TodoUpdateRequestDTO {

    // 수정할 투두 제목 (선택)
    @Schema(
            description = "수정할 투두 제목 (선택)",
            example = "자료구조 과제 다시 제출"
    )
    private String title;

    // 수정할 카테고리 ID (선택)
    @Schema(
            description = "수정할 카테고리 ID (선택)",
            example = "2",
            nullable = true
    )
    private Long categoryId;
}
