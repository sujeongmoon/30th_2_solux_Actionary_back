package com.req2res.actionarybe.domain.auth.service;

import com.req2res.actionarybe.domain.auth.dto.LoginRequestDTO;
import com.req2res.actionarybe.domain.auth.dto.LoginResponseDTO;
import com.req2res.actionarybe.domain.auth.dto.SignupRequestDTO;
import com.req2res.actionarybe.domain.auth.dto.SignupResponseDTO;
import com.req2res.actionarybe.domain.member.entity.Badge;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.BadgeRepository;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import com.req2res.actionarybe.global.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final BadgeRepository badgeRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 탈퇴 (member 남기고, 이름/닉네임 익명화)
    @Transactional
    public void withdrawMember(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 이미 탈퇴한 회원이면 404에러
        if (member.isWithdrawn()) {
            throw new CustomException(ErrorCode.WITHDRAWN_MEMBER);
        }

        // 가시적 정보만 (익명)으로 바꿈.
        member.setNickname("(익명)");
        member.setName("(익명)");

        // 탈퇴했음 표시
        member.setWithdrawn(true);
    }

    // 로그인
    public LoginResponseDTO login(LoginRequestDTO req) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getLoginId(), req.getPassword())
        );

        // 1. 로그인 인증 후, DB에서 member 정보 조회
        Member member = memberRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS));

        // 2. 이미 탈퇴한 멤버면 404에러
        if(member.isWithdrawn()){
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 3. JWT 생성 시 memberId와 loginId 모두 포함
        String accessToken = tokenProvider.createAccessToken(member.getId(), member.getLoginId());
        String refreshToken = tokenProvider.createRefreshToken(member.getLoginId());

        return new LoginResponseDTO(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                accessToken,
                refreshToken
        );
    }

    // 회원가입
    public SignupResponseDTO signup(SignupRequestDTO req) {
        // id = 1 → 0P 기본 뱃지
        Badge defaultBadge = badgeRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.BADGE_NOT_FOUND));

        // 탈퇴한 loginId로 회원가입하려고 하면, 이전에 사용했던 id는 다시 못쓴다고 exception 발생
        if (memberRepository.existsByLoginIdAndWithdrawnTrue(req.getLoginId())) {
            throw new CustomException(ErrorCode.ACCOUNT_UNRESTORABLE);
        }

        // 1. 중복 검사
        if (memberRepository.existsByLoginId(req.getLoginId())) {
            throw new CustomException(ErrorCode.LOGIN_ID_DUPLICATED);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(req.getPassword());

        // 3. 반환할 객체 만들기
        Member member = Member.builder()
                .loginId(req.getLoginId())
                .password(encodedPassword) // 암호화된 비번 넣기
                .phoneNumber(req.getPhoneNumber())
                .email(req.getEmail())
                .name(req.getName())
                .nickname("action_" + UUID.randomUUID().toString().substring(0, 8))
                .birthday(LocalDate.parse(req.getBirthday())) // 문자열 날짜 파싱
                .badge(defaultBadge)
                .build();

        // 4. DB 저장
        Member savedMember = memberRepository.save(member);

        // 5. 결과 반환 (Entity -> DTO 변환)
        // 아까 배운 from 메서드 활용!
        return SignupResponseDTO.from(savedMember);
    }
}
