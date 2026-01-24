package com.req2res.actionarybe.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String accessToken;
    private String refreshToken;
}
