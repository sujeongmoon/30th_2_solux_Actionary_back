package com.req2res.actionarybe.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateProfileRequestDTO {

    @Schema(description = "프로필 이미지 파일", type = "string", format = "binary")
    private MultipartFile profileImage;

}

