package com.req2res.actionarybe.domain.todo.dto;
//투두 생성 API에서 쓰이는 DTO


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class TodoCreateRequestDTO {

    @NotBlank(message = "할 일 내용을 입력해주세요.")
    private String title;   // 할 일 내용

    @NotNull(message = "날짜를 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date; // "2025-10-31" 형식

    // 없으면 null 허용
    private Long categoryId; // 카테고리 ID (optional)
}

