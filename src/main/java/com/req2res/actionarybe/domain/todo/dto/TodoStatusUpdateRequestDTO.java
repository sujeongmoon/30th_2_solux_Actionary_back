package com.req2res.actionarybe.domain.todo.dto;
// 투두 달성/실패 처리 API 요청 DTO

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투두 상태 변경 요청 DTO")
public class TodoStatusUpdateRequestDTO {

    // 변경할 투두 상태 (DONE 또는 FAILED)
    @Schema(
            description = "변경할 투두 상태",
            example = "DONE"
    )
    private String status;
}
