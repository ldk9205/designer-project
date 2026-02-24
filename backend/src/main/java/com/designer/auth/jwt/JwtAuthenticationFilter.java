package com.designer.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// JwtAuthenticationFilter

// 책임:
// - 모든 요청에서 Authorization 헤더를 확인
// - Bearer 토큰이 있으면:
//   1) JWT 유효성 검증
//   2) designerId 추출
//   3) request attribute에 designerId 주입 (Controller에서 @RequestAttribute로 사용)
//   4) SecurityContext에 Authentication 주입 (Spring Security 인증 처리)

// 특징 : OncePerRequestFilter: 요청 당 1회만 실행되는 필터

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorization: Bearer <token> 파싱
        String token = resolveToken(request);

        // 토큰이 존재하면 인증 시도
        if (token != null) {

            // 토큰이 유효하지 않으면 인증 실패로 처리
            if (!jwtTokenProvider.validateToken(token)) {
                // 여기서 예외가 나면 Security의 EntryPoint가 401 응답을 내려주게 됨(설정에 따라)
                throw new BadCredentialsException("Invalid JWT Token");
            }

            // 토큰에서 사용자 식별자 추출
            Long designerId = jwtTokenProvider.getDesignerId(token);
            // Controller에서 쓰기 위한 값 주입
            request.setAttribute("designerId", designerId);

            // ✅ Spring Security 인증 처리
            var auth = new UsernamePasswordAuthenticationToken(
                    // 이걸 넣어줘야 .anyRequest().authenticated()를 통과할 수 있음. 인증된 사용자로 SecurityContext가 유지됨
                    designerId, null, List.of()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer 토큰만 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }
        return null;
    }
}
