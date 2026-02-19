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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private ResponseCookie refreshCookie(String value, long days) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(false)          // ✅ 로컬 개발 http
                .sameSite("Lax")        // ✅ 로컬 same-site면 보통 OK
                .path("/auth")
                .maxAge(Duration.ofDays(days))
                .build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/auth")
                .maxAge(0)
                .build();
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequestDto dto) {
        authService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        AuthIssueResult result = authService.loginAndIssue(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.getRefreshToken(), 14).toString())
                .body(result.getBody());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        AuthIssueResult result = authService.refreshAndIssue(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.getRefreshToken(), 14).toString())
                .body(result.getBody());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        authService.logout(refreshToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<DesignerDto> getMe(@RequestAttribute("designerId") Long designerId) {
        return ResponseEntity.ok(authService.getMe(designerId));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@RequestAttribute("designerId") Long designerId) {
        authService.deleteMe(designerId);
        return ResponseEntity.noContent().build();
    }
}
