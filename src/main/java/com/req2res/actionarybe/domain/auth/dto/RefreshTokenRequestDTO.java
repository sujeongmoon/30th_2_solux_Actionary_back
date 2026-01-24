package com.req2res.actionarybe.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequestDTO {

    @Schema(description = "발급받은 Refresh Token", example = "eyJhbGciOiJIUzI1NiIsIn...")
    @NotBlank
    private String refreshToken;
}
