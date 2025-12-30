package com.req2res.actionarybe.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileRequestDTO {
    @Schema(example = "https://example.com/uploads/2025/01/28/image_abc123.jpg")
    @NotBlank(message = "imageUrl은 필수입니다.")
    String imageUrl;
}
