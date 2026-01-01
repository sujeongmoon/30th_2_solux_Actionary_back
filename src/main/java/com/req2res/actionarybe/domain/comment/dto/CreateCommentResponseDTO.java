package com.req2res.actionarybe.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CreateCommentResponseDTO {
    private Long content_id;
    private String content;
    private Boolean is_secret;
    private LocalDateTime created_at;
    private AuthorCommentDTO author;
}
