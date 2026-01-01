package com.req2res.actionarybe.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CreateCommentResponseDTO {
    private Long contentId;
    private String content;
    private Boolean isSecret;
    private LocalDateTime created_at;
    private AuthorCommentDTO author;
}
