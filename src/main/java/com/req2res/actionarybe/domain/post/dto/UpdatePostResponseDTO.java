package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePostResponseDTO {
    private Long post_id;
    private String title;
}
