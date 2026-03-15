package com.designer.config;

import com.designer.auth.jwt.JwtAuthenticationEntryPoint;
import com.designer.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * ✅ SecurityConfig
 *
 * 역할 : Spring Security의 핵심 정책을 한 곳에서 정의한다.
 *   1) CSRF 정책
 *   2) CORS 정책
 *   3) 세션 정책(STATELESS)
 *   4) 인증 실패 처리(EntryPoint)
 *   5) URL 별 접근 제어(permitAll / authenticated)
 *   6) JWT 필터를 Security Filter Chain에 삽입
 *
 * 이 프로젝트의 기본 철학 : Access Token: JWT로 stateless 인증. Refresh Token: HttpOnly 쿠키 + DB 상태 관리(별도 로직)
 * => 그래서 세션은 사용하지 않고(STATELESS), 매 요청마다 Authorization 헤더를 통해 JWT를 검증한다.
 */

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // JWT 인증 필터 : Authorization: Bearer <token> 검사. 유효하면 SecurityContext에 Authentication 주입
    // request attribute에 designerId 주입
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 인증 실패 EntryPoint : 인증이 필요하지만 인증이 없거나 실패하면 401 JSON 응답으로 통일
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // Spring Security 필터 체인 구성 :
    // 이 메서드가 "보안 정책의 핵심"이며, 스프링 부트 3 / Security 6에서 권장되는 Bean 방식 구성이다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // CORS 설정
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000")); // 허용할 프론트 Origin
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type")); // 허용할 헤더(프론트에서 보내는 헤더)
                    config.setAllowCredentials(true); // 쿠키 포함 요청 허용(refreshToken 쿠키)
                    return config;
                }))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 정책: STATELESS

                // 인증 실패 처리(EntryPoint)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // URL 접근 제어 정책
                // permitAll : 회원가입/로그인/리프레시/로그아웃은 인증 없이 접근 가능
                // authenticated : 나머지 모든 요청은 JWT 인증이 필요함
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )

                // JWT 필터 삽입 위치 : UsernamePasswordAuthenticationFilter 전에 실행되도록 배치
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
