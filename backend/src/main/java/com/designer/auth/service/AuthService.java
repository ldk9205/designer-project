package com.designer.auth.service;

import com.designer.auth.dto.*;

// 인증 / 계정(Auth) domain의 Service Interface

public interface AuthService {
    
    // 회원 가입 : 이메일 중복 체크, 비밀번호 hash, 디자이너 계정 생성
    void signup(SignupRequestDto signupRequestDto);

    // 로그인 + Token 발급
    // return type이 AuthIssueResult인 이유 :
    // accessToken은 Response Body로 내려주고 refreshToken은 HttpOnly Cookie로 내려주고 싶기 때문
    // => Service에서는 둘을 같이 발급하고 Controller가 cookie로 굽는다.
    AuthIssueResult loginAndIssue(LoginRequestDto loginRequestDto);

    // refreshToken 검증 + rotation + 새 access 발급
    AuthIssueResult refreshAndIssue(String refreshToken);

    // 로그아웃(현재 refreshToken 폐기)
    void logout(String refreshToken);
    
    // 내 정보 조회 : designerId 기준
    DesignerDto getMe(Long designerId);
    
    // 회원 탈퇴 : designers 삭제 + refresh token 전체 폐기
    void deleteMe(Long designerId);
}