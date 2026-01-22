package com.req2res.actionarybe.global.security;

import com.req2res.actionarybe.domain.member.service.CustomMemberDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityMs;

    private final CustomMemberDetailsService userDetailsService;

    public JwtTokenProvider(
            CustomMemberDetailsService userDetailsService,
            @Value("${jwt.secret:local-demo-secret-change-me-12345678901234567890}") String secret,
            @Value("${jwt.access-token-expiration-ms:3000}") long accessMs
    ) {
        this.userDetailsService = userDetailsService;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidityMs = accessMs;
    }

    public String createAccessToken(String loginId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenValidityMs);
        return Jwts.builder()
                .setSubject(loginId)
                .claim("type", "access")         // 토큰 타입
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String loginId) { // memberId: 나중에 loginId로 DB 조회해서 가져옴
        final long refreshTokenValidityMs = 1209600000; // 2주
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenValidityMs);
        return Jwts.builder()
                .setSubject(loginId)
                .claim("type", "refresh")        // Access Token과 구분
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isRefreshToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return "refresh".equals(claims.get("type"));
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {

        String loginId = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        // CustomUserDetailsService를 통해 CustomUserDetails 생성
        CustomUserDetails userDetails =
                (CustomUserDetails) userDetailsService.loadUserByUsername(loginId);

        // principal에 CustomUserDetails 넣기
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    public String getLoginIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
