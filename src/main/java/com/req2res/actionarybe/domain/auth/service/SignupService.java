package com.req2res.actionarybe.domain.auth.service;

import com.req2res.actionarybe.domain.auth.dto.SignupRequestDTO;
import com.req2res.actionarybe.domain.auth.dto.SignupResponseDTO;
import com.req2res.actionarybe.domain.user.entity.User;
import com.req2res.actionarybe.domain.user.repository.UserRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDTO signup(SignupRequestDTO req) {

        // 1. 중복 검사
        if (userRepository.existsByLoginId(req.getLoginId())) {
            throw new CustomException(ErrorCode.LOGIN_ID_DUPLICATED);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(req.getPassword());

        // 3. 반환할 객체 만들기
        User user = User.builder()
                .loginId(req.getLoginId())
                .password(encodedPassword) // 암호화된 비번 넣기
                .phoneNumber(req.getPhoneNumber())
                .email(req.getEmail())
                .name(req.getName())
                .nickname("action_" + UUID.randomUUID().toString().substring(0, 8))
                .birthday(LocalDate.parse(req.getBirthday())) // 문자열 날짜 파싱
                .build();

        // 4. DB 저장
        User savedUser = userRepository.save(user);

        // 5. 결과 반환 (Entity -> DTO 변환)
        // 아까 배운 from 메서드 활용!
        return SignupResponseDTO.from(savedUser);
    }
}
