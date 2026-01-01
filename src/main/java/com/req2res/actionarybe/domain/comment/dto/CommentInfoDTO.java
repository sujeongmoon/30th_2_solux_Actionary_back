package com.req2res.actionarybe.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentInfoDTO {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isSecret;
    private AuthorDTO author;

    @Getter
    @AllArgsConstructor
    public static class AuthorDTO {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
        private Long badge_id;
    }
}
