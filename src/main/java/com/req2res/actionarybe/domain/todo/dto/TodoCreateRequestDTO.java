package com.req2res.actionarybe.domain.todo.dto;
// 투두 생성 API에서 사용하는 요청 DTO

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "투두 생성 요청 DTO")
public class TodoCreateRequestDTO {

    // 할 일 내용
    @Schema(
            description = "할 일 내용",
            example = "자료구조 과제 제출"
    )
    @NotBlank(message = "할 일 내용을 입력해주세요.")
    private String title;

    // 투두 날짜 (yyyy-MM-dd)
    @Schema(
            description = "투두 날짜",
            example = "2025-10-31"
    )
    @NotNull(message = "날짜를 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    // 카테고리 ID (선택)
    @Schema(
            description = "카테고리 ID (선택)",
            example = "1",
            nullable = true
    )
    private Long categoryId;
}
