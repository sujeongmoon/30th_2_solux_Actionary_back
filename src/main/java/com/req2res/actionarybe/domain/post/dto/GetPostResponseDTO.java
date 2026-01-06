package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetPostResponseDTO {
    PostInfoDTO post;
    List<String> postImageUrls;
    PostAuthorDTO author;
}
