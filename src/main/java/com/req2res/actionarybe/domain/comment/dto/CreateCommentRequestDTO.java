package com.req2res.actionarybe.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateCommentRequestDTO {
    @NotBlank
    @Schema(example = "이것은 댓글 내용입니다.")
    private String content;

    @NotBlank
    @Schema(example = "true")
    private Boolean isSecret;
}
