package com.req2res.actionarybe.domain.todo.dto;
//할일 생성 시에 받는 responseDTO

import com.fasterxml.jackson.annotation.JsonFormat;
import com.req2res.actionarybe.domain.todo.entity.Todo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TodoCreateResponseDTO {

    private Long todoId;
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long categoryId;
    private String status;          // PENDING / DONE / FAILED

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

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

