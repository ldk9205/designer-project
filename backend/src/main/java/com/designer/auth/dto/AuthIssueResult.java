package com.designer.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// # token 발급 결과를 "서버 내부"에서 다루기 위한 wrapper DTO

// - Service는 access + refresh 를 함께 발급
// - Controller는 refreshToken은 HttpOnly cookie로 내려주고, body(AuthResponseDto)만 response body로 return하기 위해 이 형태를 사용
// - 사용처 : AuthService.loginAndIssue(), AuthService.refreshAndIssue()

@Getter
@AllArgsConstructor
public class AuthIssueResult {
    private AuthResponseDto body; // front로 내려줄 Response body(accessToken + designer)
    private String refreshToken; // refresh token 값(cookie로 내려주기 위한 용도). Response body에 넣지 않는 것이 보안상 일반적 
}
