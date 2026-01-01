package com.req2res.actionarybe.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class LoginRequestDTO {
    @NotBlank(message = "loginId는 필수입니다.")
    @Schema(example = "user1234")
    private String loginId;

    @NotBlank(message = "password는 필수입니다.")
    @Schema(example = "securePassword123!")
    private String password;
}

