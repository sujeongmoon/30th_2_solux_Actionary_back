package com.req2res.actionarybe.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostContentRequestDTO {

    @Schema(
            description = "게시글 본문 내용",
            example = "자료구조 스터디 완료"
    )
    @NotBlank
    private String text;

    @Schema(
            description = "게시글에 포함된 이미지 URL 목록",
            example = "[\"https://storage.com/aaa.jpg\", \"https://storage.com/bbb.jpg\"]"
    )
    @NotBlank
    private List<String> imageUrls;
}

