package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostSummaryDTO {
    private Long postId;
    private String type;
    private String title;
    private String nickname;
    private int commentCount;
    private LocalDateTime createdAt;
}
