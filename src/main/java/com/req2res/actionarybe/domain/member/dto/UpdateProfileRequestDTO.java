package com.req2res.actionarybe.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileRequestDTO {
    @NotBlank(message = "imageUrl은 필수입니다.")
    String imageUrl;
}
