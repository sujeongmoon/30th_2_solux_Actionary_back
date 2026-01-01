package com.req2res.actionarybe.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateNicknameResponseDTO {
    private Long id;
    private String nickname;
}
