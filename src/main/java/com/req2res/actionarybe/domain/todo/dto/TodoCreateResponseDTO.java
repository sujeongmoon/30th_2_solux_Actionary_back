package com.req2res.actionarybe.domain.todo.dto;
// 투두 생성 API 응답 DTO

import com.fasterxml.jackson.annotation.JsonFormat;
import com.req2res.actionarybe.domain.todo.entity.Todo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "투두 생성 응답 DTO")
public class TodoCreateResponseDTO {

    // 투두 고유 ID
    @Schema(description = "투두 ID", example = "10")
    private Long todoId;

    // 할 일 내용
    @Schema(description = "할 일 내용", example = "자료구조 과제 제출")
    private String title;

    // 투두 날짜 (yyyy-MM-dd)
    @Schema(description = "투두 날짜", example = "2025-10-31")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    // 카테고리 ID (없으면 null)
    @Schema(description = "카테고리 ID", example = "1", nullable = true)
    private Long categoryId;

    // 투두 상태 (PENDING / DONE / FAILED)
    @Schema(description = "투두 상태", example = "PENDING")
    private String status;

    // 투두 생성 시각
    @Schema(description = "투두 생성 일시", example = "2025-10-30T22:15:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // To-do 엔티티 → 응답 DTO 변환
    public static TodoCreateResponseDTO from(Todo todo) {
        return TodoCreateResponseDTO.builder()
                .todoId(todo.getId())
                .title(todo.getTitle())
                .date(todo.getDate())
                .categoryId(todo.getCategoryId())
                .status(todo.getStatus().name())
                .createdAt(todo.getCreatedAt())
                .build();
    }
}
