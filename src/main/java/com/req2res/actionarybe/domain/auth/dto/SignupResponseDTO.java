package com.req2res.actionarybe.domain.auth.dto;

import com.req2res.actionarybe.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponseDTO {

    private Long userId;
    private String loginId;
    private String nickname;

    public static SignupResponseDTO from(User user) {
        return SignupResponseDTO.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .build();
    }
}