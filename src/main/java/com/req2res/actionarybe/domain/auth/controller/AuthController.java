package com.req2res.actionarybe.domain.auth.controller;

import com.req2res.actionarybe.domain.user.repository.UserRepository;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.domain.auth.dto.*;
import com.req2res.actionarybe.global.security.JwtTokenProvider;
import com.req2res.actionarybe.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO req) {
        // 로그인 인증 (Security 내부가 비밀번호 비교함)
        authManager.authenticate( //오류나면? -> id는 DB에 있는데, pw 잘못됐을 가능성 높음
                new UsernamePasswordAuthenticationToken(req.getLoginId(), req.getPassword())
        );

        // JWT 토큰 생성
        String token = tokenProvider.createToken(req.getLoginId());

        // DB에서 유저 조회
        User u = userRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new BadCredentialsException("Invalid loginId"));

        // 응답 데이터 객체 생성
        LoginResponseDTO data = new LoginResponseDTO(
                u.getId(),
                u.getNickname(),
                u.getImageUrl(),
                token
        );

        // 최종 응답 반환
        return ResponseEntity.ok(Response.success("로그인에 성공하였습니다.", data));
    }
}
