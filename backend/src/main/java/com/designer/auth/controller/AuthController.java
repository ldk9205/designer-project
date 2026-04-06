package com.designer.auth.controller;

import com.designer.auth.dto.*;
import com.designer.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

// 역할(HTTP layer 책임):
// - 요청 DTO 검증(@Valid)
// - 요청 DTO 검증(@Valid)
// - refreshToken을 HttpOnly Cookie로 세팅/삭제
// - Service가 반환한 결과(AuthIssueResult)에서 body는 JSON Response Body로 refreshToken은 Set-Cookie 헤더로 내려주는 정책 처리

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // refreshToken cookie 생성 helper
    private ResponseCookie refreshCookie(String value, long days) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true) // JS에서 접근 불가(XSS 방어)
                .secure(false) // “HTTPS에서만 쿠키 전송” 옵션. 로컬 개발(http) 환경용
                .sameSite("Lax") // 기본적인 CSRF 방어 + 같은 사이트 요청에선 쿠키 포함
                .path("/") // /auth로 시작하는 요청에만 쿠키 전송(스코프 최소화)
                .maxAge(Duration.ofDays(days)) // 쿠키 만료 기간 (refresh token 만료와 맞추는 게 일반적)
                .build();
    }

    // refreshToken cookie 제거 helper : 동일한 name/path로 maxAge=0 설정하여 브라우저에 삭제 지시
    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }

    // 회원가입 : Request Body 검증(@Valid). 성공 시 201 Created, body 없음
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequestDto dto) {
        authService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 로그인

    // 응답 정책:
    // - refreshToken: HttpOnly 쿠키(Set-Cookie 헤더)
    // - accessToken + designer: JSON 바디(AuthResponseDto)

    // 프론트 관점:
    // - accessToken은 상태 저장(메모리/스토리지 등) 후 Authorization 헤더로 사용
    // - refreshToken은 브라우저 쿠키로 자동 관리(클라이언트 JS가 직접 못 건드림)
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        AuthIssueResult result = authService.loginAndIssue(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.getRefreshToken(), 14).toString())
                .body(result.getBody());
    }

    // 토큰 재발급(refresh)
    // 입력 : refreshToken 쿠키(@CookieValue)에서 읽음
    // 처리 : Service가 refreshToken 유효성 검사 + rotation + 새 access 발급
    // 응답 : 새 refreshToken 쿠키로 교체(Set-Cookie). 새 accessToken + designer는 body로 return.
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        AuthIssueResult result = authService.refreshAndIssue(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.getRefreshToken(), 14).toString())
                .body(result.getBody());
    }

    // logout
    // 처리 : refreshToken이 있으면 DB에서 revoked 처리. 쿠키를 삭제(maxAge=0)하여 브라우저에서도 제거
    // response : 204 No Content
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        authService.logout(refreshToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString())
                .build();
    }

    // 내 정보 조회
    // 입력 : @RequestAttribute("designerId") -> JwtAuthenticationFilter가 accessToken 검증 후 request에 심어줌
    // 응답 : DesignerDto (비밀번호 없음)
    @GetMapping("/me")
    public ResponseEntity<DesignerDto> getMe(@RequestAttribute("designerId") Long designerId) {
        return ResponseEntity.ok(authService.getMe(designerId));
    }

    @PutMapping("/me")
    public ResponseEntity<DesignerDto> updateMe(
            @RequestAttribute("designerId") Long designerId,
            @Valid @RequestBody DesignerUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(authService.updateMe(designerId, dto));
    }

    // 회원 탈퇴
    // 입력 : @RequestAttribute("designerId") (JWT 인증된 사용자)
    // 처리 : designers 삭제 + refresh 토큰 전체 revoke
    // 응답 : 204 No Content
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@RequestAttribute("designerId") Long designerId) {
        authService.deleteMe(designerId);
        return ResponseEntity.noContent().build();
    }
}
