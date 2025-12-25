package com.req2res.actionarybe.domain.auth.service;

import com.req2res.actionarybe.domain.auth.dto.LoginRequestDTO;
import com.req2res.actionarybe.domain.auth.dto.LoginResponseDTO;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public LoginResponseDTO login(LoginRequestDTO req) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getLoginId(), req.getPassword())
        );

        // 1. 로그인 인증 후, DB에서 member 정보 조회
        Member member = memberRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new BadCredentialsException("Invalid loginId"));

        // 2. JWT 생성 시 memberId와 loginId 모두 포함
        String accessToken = tokenProvider.createToken(member.getId(), member.getLoginId());
        String refreshToken = tokenProvider.createRefreshToken(member.getLoginId());


        return new LoginResponseDTO(
                member.getId(),
                member.getNickname(),
                member.getImageUrl(),
                accessToken,
                refreshToken
        );
    }
}

