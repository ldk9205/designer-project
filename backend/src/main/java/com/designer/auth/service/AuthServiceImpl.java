package com.designer.auth.service;

import com.designer.auth.dto.AuthResponseDto;
import com.designer.auth.dto.DesignerDto;
import com.designer.auth.dto.LoginRequestDto;
import com.designer.auth.dto.SignupRequestDto;
import com.designer.auth.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.designer.auth.dto.DesignerAuthDto;
import com.designer.auth.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Override
    public void signup(SignupRequestDto signupRequestDto) {

        // 이메일 중복 체크
        int count = authMapper.countByEmail(signupRequestDto.getEmail());
        if (count > 0) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword =
                passwordEncoder.encode(signupRequestDto.getPassword());
        signupRequestDto.setPassword(encodedPassword);

        // DB 저장
        authMapper.insertDesigner(signupRequestDto);
    }

    /**
     * 로그인
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {

        // 🔐 로그인 검증 전용 DTO로 조회
        DesignerAuthDto authDesigner =
                authMapper.findAuthByEmail(loginRequestDto.getEmail());

        if (authDesigner == null) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 비밀번호 검증
        boolean match = passwordEncoder.matches(
                loginRequestDto.getPassword(),
                authDesigner.getPassword()
        );

        if (!match) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT 발급
        String accessToken =
                jwtTokenProvider.createToken(
                        authDesigner.getId(),
                        authDesigner.getEmail()
                );

        // ✅ 응답용 DTO 구성 (password 없음)
        DesignerDto designerDto = new DesignerDto();
        designerDto.setId(authDesigner.getId());
        designerDto.setEmail(authDesigner.getEmail());
        designerDto.setName(authDesigner.getName());
        // phone, createdAt 필요하면 추가 세팅

        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(accessToken);
        response.setDesigner(designerDto);

        return response;
    }


    /**
     * 내 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public DesignerDto getMe(Long designerId) {
        return authMapper.findById(designerId);
    }

    /**
     * 회원 탈퇴
     */
    @Override
    public void deleteMe(Long designerId) {
        authMapper.deleteById(designerId);
    }
}
