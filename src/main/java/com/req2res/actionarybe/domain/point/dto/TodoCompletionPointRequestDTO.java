package com.req2res.actionarybe.domain.point.dto;
//투두 완료 포인트 적립 API 에서 사용하는 requestDTO

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투두 완료 포인트 적립 요청 DTO")
public class TodoCompletionPointRequestDTO {

    @Schema(description = "완료한 투두 ID", example = "55", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "todoId는 필수입니다.")
    private Long todoId;

    public TodoCompletionPointRequestDTO(Long todoId) {
        this.todoId = todoId;
    }
}
