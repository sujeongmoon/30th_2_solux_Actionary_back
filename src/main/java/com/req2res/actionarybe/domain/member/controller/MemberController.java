package com.req2res.actionarybe.domain.member.controller;

import com.req2res.actionarybe.domain.member.dto.*;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/info")
    @SecurityRequirement(name = "Bearer Token")
    public ResponseEntity<Response<LoginMemberResponseDTO>> info(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long id = userDetails.getId();
        LoginMemberResponseDTO result = memberService.getLoginMemberInfo(id);
        return ResponseEntity.ok(Response.success("로그인 유저 정보 조회 성공", result));
    }

    @PatchMapping("/profile")
    public ResponseEntity<Response<UpdateProfileRequestDTO>> profile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateProfileRequestDTO updateProfileRequestDTO
    ) {
        Long id=userDetails.getId();
        memberService.updateProfile(id,updateProfileRequestDTO.getImageUrl());
        return ResponseEntity.ok(Response.success("프로필 사진 변경에 성공했습니다.",null));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<Response<UpdateNicknameResponseDTO>> nickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateNicknameRequestDTO updateNickNameRequestDTO
    ){
        Long id = userDetails.getId();
        String nickname=updateNickNameRequestDTO.getNickname();
        UpdateNicknameResponseDTO result=memberService.updateNickname(id,nickname);
        return ResponseEntity.ok(Response.success("닉네임 변경에 성공했습니다.",result));
    }

    @GetMapping("/badge")
    public ResponseEntity<Response<BadgeResponseDTO>> badge(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long id=userDetails.getId();
        BadgeResponseDTO result = memberService.badge(id);
        return ResponseEntity.ok(Response.success("뱃지 정보 조회에 성공했습니다.",result));
    }
}
