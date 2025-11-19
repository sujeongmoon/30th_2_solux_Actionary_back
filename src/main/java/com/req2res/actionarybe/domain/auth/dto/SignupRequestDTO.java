package com.req2res.actionarybe.domain.auth.dto;

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
    private String profileImageUrl;

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String birthday;
}