package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostAuthorDTO {
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private Long badge;
}
