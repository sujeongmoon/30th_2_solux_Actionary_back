package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostInfoDTO {
    private Long postId;
    private String type;
    private String title;
    private String textContent;
    private int commentCount;
    private LocalDateTime createdAt;
}
