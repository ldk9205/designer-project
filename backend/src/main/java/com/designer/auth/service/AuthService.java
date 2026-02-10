package com.designer.auth.service;

import com.designer.auth.dto.AuthResponseDto;
import com.designer.auth.dto.DesignerDto;
import com.designer.auth.dto.LoginRequestDto;
import com.designer.auth.dto.SignupRequestDto;

public interface AuthService {

    /**
     * 회원가입
     */
    void signup(SignupRequestDto signupRequestDto);

    /**
     * 로그인
     * @return accessToken + 디자이너 정보
     */
    AuthResponseDto login(LoginRequestDto loginRequestDto);

    /**
     * 내 정보 조회 (/auth/me)
     */
    DesignerDto getMe(Long designerId);

    /**
     * 회원 탈퇴 (/auth/me DELETE)
     */
    void deleteMe(Long designerId);
}