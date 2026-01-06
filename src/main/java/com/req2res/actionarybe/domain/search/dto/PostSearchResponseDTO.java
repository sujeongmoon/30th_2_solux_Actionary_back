package com.req2res.actionarybe.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostSearchResponseDTO {
    private Long postId;
    private String type;
    private String title;
    private String authorNickname;
    private LocalDateTime createdAt;
    private int commentCount;
    private boolean isMine;
}
