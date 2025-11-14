package com.req2res.actionarybe.login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PwEncryptTest {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password456";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("암호화된 비밀번호: " + encodedPassword);
        System.out.println("일치 여부 확인: " + encoder.matches("password456", encodedPassword));
    }
}

