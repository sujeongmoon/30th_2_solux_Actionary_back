package com.req2res.actionarybe.domain.auth.service;

import com.req2res.actionarybe.domain.auth.dto.LoginRequestDTO;
import com.req2res.actionarybe.domain.auth.dto.LoginResponseDTO;
import com.req2res.actionarybe.domain.Member.entity.Member;
import com.req2res.actionarybe.domain.Member.repository.MemberRepository;
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

        String accessToken = tokenProvider.createToken(req.getLoginId());
        String refreshToken = tokenProvider.createRefreshToken(req.getLoginId());

        Member member = memberRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new BadCredentialsException("Invalid loginId"));

        return new LoginResponseDTO(
                member.getId(),
                member.getNickname(),
                member.getImageUrl(),
                accessToken,
                refreshToken
        );
    }
}

