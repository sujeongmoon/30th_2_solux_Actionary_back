package com.req2res.actionarybe.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostContentRequestDTO {

    @Schema(
            description = "게시글 본문 내용",
            example = "자료구조 스터디\n완료"
    )
    @NotBlank(message = "게시글 텍스트 작성은 필수입니다.")
    private String text;

    @Schema(
            description = "게시글에 업로드할 이미지 파일 목록",
            type = "array",
            format = "binary"
    )
    private List<MultipartFile> images;

}

