package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostAuthorDTO {
    private Long member_id;
    private String nickname;
    private String profile_image_url;
    private Long badge;
}
