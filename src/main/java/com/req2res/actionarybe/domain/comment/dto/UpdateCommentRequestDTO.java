package com.req2res.actionarybe.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCommentRequestDTO {
    @Schema(example = "수정된 댓글")
    private String content;
    @Schema(example = "true")
    private Boolean isSecret;
}
