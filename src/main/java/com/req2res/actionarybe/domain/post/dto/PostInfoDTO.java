package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostInfoDTO {
    private Long id;
    private String type;
    private String title;
    private String text;
    private int commentsCount;
    private LocalDateTime createdAt;
}
