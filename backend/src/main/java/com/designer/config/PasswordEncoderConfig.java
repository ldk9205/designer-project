package com.designer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoderConfig
 *
 * 역할:
 * - 비밀번호를 평문으로 저장하지 않고, 안전한 해시(BCrypt)로 저장/검증하기 위한 Bean 등록
 *
 * 사용처:
 * - AuthServiceImpl.signup(): encode(평문) → 해시 저장
 * - AuthServiceImpl.loginAndIssue(): matches(평문, 해시) → 로그인 검증
 */

@Configuration
public class PasswordEncoderConfig {

    // PasswordEncoder Bean : Spring이 DI로 주입 가능해짐.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
