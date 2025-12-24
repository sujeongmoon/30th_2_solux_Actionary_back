package com.req2res.actionarybe.domain.auth.controller;

import com.req2res.actionarybe.domain.auth.service.LoginService;
import com.req2res.actionarybe.domain.auth.service.SignupService;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.domain.auth.dto.*;
import com.req2res.actionarybe.global.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final SignupService signupService;
    private final LoginService loginService;

    @PostMapping("/signup")
    public ResponseEntity<Response<SignupResponseDTO>> signup(@Valid @RequestBody SignupRequestDTO req){
        SignupResponseDTO result = signupService.signup(req);
        return ResponseEntity.ok(Response.success("회원가입에 성공하였습니다.", result));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO req) {

        LoginResponseDTO result = loginService.login(req);
        return ResponseEntity.ok(Response.success("로그인에 성공하였습니다.", result));
    }
}
