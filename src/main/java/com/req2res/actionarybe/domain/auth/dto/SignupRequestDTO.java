package com.req2res.actionarybe.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignupRequestDTO {

    @NotBlank
    @Schema(example = "https://example.com/images/profile.png")
    private String profileImageUrl;

    @NotBlank
    @Schema(example = "user1234")
    private String loginId;

    @NotBlank
    @Schema(example = "securePassword123!")
    private String password;

    @NotBlank
    @Schema(example = "01012345678")
    private String phoneNumber;

    @NotBlank
    @Email
    @Schema(example = "user@example.com")
    private String email;

    @NotBlank
    @Schema(example = "홍길동")
    private String name;

    @NotBlank
    @Schema(example = "2000-01-01", type = "string", format = "date")
    private String birthday;
}
