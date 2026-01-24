package com.req2res.actionarybe.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorCommentDTO {
    private Long memberId;
    private String nickname;
}
