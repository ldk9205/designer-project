package com.designer.auth.service;

import com.designer.auth.dto.*;

public interface AuthService {

    void signup(SignupRequestDto signupRequestDto);

    // ✅ 로그인 + refreshToken 발급(쿠키로 내려주기 위해 refreshToken도 함께 리턴)
    AuthIssueResult loginAndIssue(LoginRequestDto loginRequestDto);

    // ✅ refreshToken 검증 + rotation + 새 access 발급
    AuthIssueResult refreshAndIssue(String refreshToken);

    // ✅ 로그아웃(현재 refreshToken 폐기)
    void logout(String refreshToken);

    DesignerDto getMe(Long designerId);

    void deleteMe(Long designerId);
}