package com.designer.auth.controller;

import com.designer.auth.dto.AuthResponseDto;
import com.designer.auth.dto.DesignerDto;
import com.designer.auth.dto.LoginRequestDto;
import com.designer.auth.dto.SignupRequestDto;
import com.designer.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        authService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto
    ) {
        AuthResponseDto response =
                authService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    // 내 정보 조회(실제로는 JwtFilter에서 designerId를 추출해서 넘김)
    @GetMapping("/me")
    public ResponseEntity<DesignerDto> getMe(
            @RequestAttribute("designerId") Long designerId
    ) {
        DesignerDto me = authService.getMe(designerId);
        return ResponseEntity.ok(me);
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(
            @RequestAttribute("designerId") Long designerId
    ) {
        authService.deleteMe(designerId);
        return ResponseEntity.noContent().build();
    }
}