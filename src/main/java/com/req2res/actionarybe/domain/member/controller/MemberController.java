package com.req2res.actionarybe.domain.member.controller;

import com.req2res.actionarybe.domain.member.dto.*;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 로그인 유저 정보 조회
    @GetMapping("/me/info")
    @SecurityRequirement(name = "Bearer Token")
    public Response<LoginMemberResponseDTO> meInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long id = userDetails.getId();
        LoginMemberResponseDTO result = memberService.getLoginMemberInfo(id);
        return Response.success("로그인 유저 정보 조회 성공", result);
    }

    // 타인 정보 조회
    @GetMapping("/{memberId}")
    public Response<OtherMemberResponseDTO> memberInfo(){

    }

    // 프로필 사진 수정
    @PatchMapping("me/profile")
    public Response<UpdateProfileRequestDTO> profile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateProfileRequestDTO updateProfileRequestDTO
    ) {
        Long id=userDetails.getId();
        memberService.updateProfile(id,updateProfileRequestDTO.getImageUrl());
        return Response.success("프로필 사진 변경에 성공했습니다.",null);
    }

    // 닉네임 수정
    @PatchMapping("me/nickname")
    public Response<UpdateNicknameResponseDTO> nickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateNicknameRequestDTO updateNickNameRequestDTO
    ){
        Long id = userDetails.getId();
        String nickname=updateNickNameRequestDTO.getNickname();
        UpdateNicknameResponseDTO result=memberService.updateNickname(id,nickname);
        return Response.success("닉네임 변경에 성공했습니다.",result);
    }

    // 뱃지 조회
    @GetMapping("me/badge")
    public Response<BadgeResponseDTO> badge(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long id=userDetails.getId();
        BadgeResponseDTO result = memberService.badge(id);
        return Response.success("뱃지 정보 조회에 성공했습니다.",result);
    }
}
