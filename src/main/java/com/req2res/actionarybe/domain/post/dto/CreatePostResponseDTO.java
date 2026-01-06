package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CreatePostResponseDTO {
    private Long id;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;
}
