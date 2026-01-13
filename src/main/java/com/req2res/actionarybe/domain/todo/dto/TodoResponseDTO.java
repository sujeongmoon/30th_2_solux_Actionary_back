package com.req2res.actionarybe.domain.todo.dto;
// 특정 날짜 투두 목록 조회 API, 투두 수정 API에서 사용하는 개별 투두 응답 DTO

import com.req2res.actionarybe.domain.todo.entity.Todo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "개별 투두 응답 DTO")
public class TodoResponseDTO {

    // 투두 고유 ID
    @Schema(description = "투두 ID", example = "1")
    private Long todoId;

    // 할 일 내용
    @Schema(description = "할 일 내용", example = "자료구조 과제 제출")
    private String title;

    // 카테고리 ID (없으면 null)
    @Schema(description = "카테고리 ID", example = "1", nullable = true)
    private Long categoryId;

    // 투두 상태 (PENDING / DONE / FAILED)
    @Schema(description = "투두 상태", example = "PENDING")
    private String status;

    // To-do 엔티티 → 응답 DTO 변환
    public static TodoResponseDTO from(Todo todo) {
        return TodoResponseDTO.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .categoryId(todo.getCategoryId())
                .status(todo.getStatus().name())
                .build();
    }
}
