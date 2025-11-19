package com.req2res.actionarybe.domain.todo.dto;
//'특정 날짜 투두 목록 조회 API'에서 사용하는 개별 투두 response DTO

import com.req2res.actionarybe.domain.todo.entity.Todo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoResponseDTO {

    private Long todoId;     // 투두 ID
    private String title;    // 할 일 내용
    private Long categoryId; // 카테고리 ID
    private String status;   // PENDING | DONE | FAILED

    // 엔티티 -> DTO 변환 정적 메서드
    public static TodoResponseDTO from(Todo todo) {
        return TodoResponseDTO.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .categoryId(todo.getCategoryId())
                .status(todo.getStatus().name())
                .build();
    }
}
