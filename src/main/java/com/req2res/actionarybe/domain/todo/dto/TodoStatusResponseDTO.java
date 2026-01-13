package com.req2res.actionarybe.domain.todo.dto;
// 투두 달성/실패 처리 API 응답 DTO

import com.req2res.actionarybe.domain.todo.entity.Todo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "투두 상태 변경 응답 DTO")
public class TodoStatusResponseDTO {

    // 투두 고유 ID
    @Schema(description = "투두 ID", example = "1")
    private Long todoId;

    // 변경된 투두 상태 (DONE / FAILED)
    @Schema(description = "변경된 투두 상태", example = "DONE")
    private String status;

    // To-do 엔티티 → 응답 DTO 변환
    public static TodoStatusResponseDTO from(Todo todo) {
        return TodoStatusResponseDTO.builder()
                .todoId(todo.getId())
                .status(todo.getStatus().name())
                .build();
    }
}
