package com.designer.auth.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증 실패 시 401 응답 포맷 통일
// 연결 위치 : SecurityConfig.exceptionHandling(authenticationEntryPoint(...))

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        // HTTP 401 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // JSON 응답 설정
        response.setContentType("application/json;charset=UTF-8");

        // 응답 바디 출력(프론트에서 에러 처리하기 좋게 포맷 통일)
        response.getWriter().write("""
            {
              "status": 401,
              "error": "UNAUTHORIZED",
              "message": "인증이 필요합니다. JWT 토큰을 확인하세요."
            }
        """);
    }
}
