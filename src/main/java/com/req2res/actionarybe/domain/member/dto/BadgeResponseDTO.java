package com.req2res.actionarybe.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class BadgeResponseDTO {
    private Long id;
    private Long memberId;
    private String name;
    private Long requiredPoint;
    private String imageUrl;
}
