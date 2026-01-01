package com.req2res.actionarybe.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorCommentDTO {
    @JsonProperty(value = "member_id")
    private Long memberId;
    private String nickname;
}
