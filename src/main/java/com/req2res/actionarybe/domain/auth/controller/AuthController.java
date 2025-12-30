package com.req2res.actionarybe.domain.auth.controller;

import com.req2res.actionarybe.domain.auth.service.AuthService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.domain.auth.dto.*;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import com.req2res.actionarybe.global.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Response<SignupResponseDTO>> signup(@Valid @RequestBody SignupRequestDTO req){
        SignupResponseDTO result = authService.signup(req);
        return ResponseEntity.ok(Response.success("회원가입에 성공하였습니다.", result));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO req) {
        LoginResponseDTO result = authService.login(req);
        return ResponseEntity.ok(Response.success("로그인에 성공하였습니다.", result));
    }

    // 회원 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<Response<Void>> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long id = userDetails.getId();
        authService.withdrawMember(id);
        return ResponseEntity.ok(Response.success("회원 탈퇴에 성공하였습니다.",null));
    }

    // 로그인 유지 (refreshToken 활용)
    @PostMapping("/refresh")
    public ResponseEntity<Response<RefreshTokenResponseDTO>> refreshAccessToken(
            @RequestBody @Valid RefreshTokenRequestDTO request
    ) {
        String refreshToken = request.getRefreshToken();

        // Refresh Token 유효성 검사
        if (!tokenProvider.validate(refreshToken)) {
            return ResponseEntity.badRequest().body(Response.fail("Refresh Token이 유효하지 않습니다."));
        }

        // loginId 추출
        String loginId = tokenProvider.getLoginIdFromToken(refreshToken);

        // 새 access token 생성
        String newAccessToken = tokenProvider.createToken(null, loginId); // id 없이 생성

        return ResponseEntity.ok(Response.success("AccessToken 재발급 완료",
                new RefreshTokenResponseDTO(newAccessToken)));
    }
}
