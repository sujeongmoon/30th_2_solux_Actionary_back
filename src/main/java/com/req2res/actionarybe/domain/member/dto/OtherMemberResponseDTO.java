package com.req2res.actionarybe.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OtherMemberResponseDTO {
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
}
