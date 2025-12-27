package com.req2res.actionarybe.domain.member.controller;

import com.req2res.actionarybe.domain.member.dto.LoginMemberResponseDTO;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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
}
