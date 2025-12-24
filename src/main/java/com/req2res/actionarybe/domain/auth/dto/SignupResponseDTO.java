package com.req2res.actionarybe.domain.auth.dto;

import com.req2res.actionarybe.domain.Member.entity.Member;
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

    public static SignupResponseDTO from(Member member) {
        return SignupResponseDTO.builder()
                .userId(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .build();
    }
}