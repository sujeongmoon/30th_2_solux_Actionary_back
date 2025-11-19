package com.req2res.actionarybe.auth;

import com.req2res.actionarybe.domain.auth.dto.SignupRequestDTO;
import com.req2res.actionarybe.domain.auth.service.SignupService;
import com.req2res.actionarybe.domain.user.entity.User;
import com.req2res.actionarybe.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest  // 1. 스프링 서버를 실제로 띄운다 (모든 설정 로드)
@Transactional   // 롤백
class SignupServiceTest {

    @Autowired // 3. 실제 서비스와 리포지토리를 가져온다
    SignupService signupService;

    @Autowired
    UserRepository userRepository;

    @Test
    void 회원가입이_성공_test() {
        SignupRequestDTO req = SignupRequestDTO.builder()
                .profileImageUrl("https://example.com/images/default.png")
                .loginId("testUser")
                .password("1234")
                .phoneNumber("010-1234-5678")
                .email("test@email.com")
                .name("홍길동")
                .birthday("2000-01-01")
                .build();

        signupService.signup(req);
        User user = userRepository.findByLoginId(req.getLoginId()).orElse(null);

        System.out.println("가입된 유저 닉네임: " + user.getNickname());

        assertThat(user).isNotNull(); // 유저가 없으면 안됨
        assertThat(user.getLoginId()).isEqualTo(req.getLoginId()); // 아이디가 똑같아야 함
    }
}
