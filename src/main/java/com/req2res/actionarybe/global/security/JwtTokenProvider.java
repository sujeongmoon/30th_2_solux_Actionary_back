package com.req2res.actionarybe.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityMs;

    public JwtTokenProvider(
            @Value("${jwt.secret:local-demo-secret-change-me-12345678901234567890}") String secret,
            @Value("${jwt.access-token-expiration-ms:3600000}") long accessMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidityMs = accessMs;
    }

    public String createToken(String loginId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenValidityMs);
        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String loginId) {
        final long refreshTokenValidityMs = 1209600000; // 2주
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenValidityMs);
        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) { return false; }
    }

    public Authentication getAuthentication(String token) {
        String loginId = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
        var principal = new User(loginId, "", AuthorityUtils.createAuthorityList("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }
}
