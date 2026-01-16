package com.req2res.actionarybe.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LoginMemberResponseDTO {
    Long memberId;
    String profileImageUrl;
    String nickname;
    String phoneNumber;
    LocalDate Birthday;
}
