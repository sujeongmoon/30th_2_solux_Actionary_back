package com.req2res.actionarybe.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentResponseDTO {
    private Long contentId;
    private String content;
    private Boolean isSecret;
    private LocalDateTime createdAt;
    private AuthorCommentDTO author;
}
