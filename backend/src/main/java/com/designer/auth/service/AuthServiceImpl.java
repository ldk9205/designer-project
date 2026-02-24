package com.designer.auth.service;

import com.designer.auth.dto.*;
import com.designer.auth.jwt.JwtTokenProvider;
import com.designer.auth.mapper.AuthMapper;
import com.designer.auth.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthMapper authMapper; // designers table 접근
    private final RefreshTokenMapper refreshTokenMapper; // refresh tokens table 접근
    private final PasswordEncoder passwordEncoder; // 비밀번호 hash/검증(Bcrypt 등)
    private final JwtTokenProvider jwtTokenProvider; // JWT 생성/검증 유틸(Access Token))

    private static final long REFRESH_DAYS = 14; // refresh token 유효 기간 정책(14일)

    // refresh token을 "예측 불가능한 값"으로 생성
    private String newRefreshTokenValue() {
        byte[] bytes = new byte[48]; // 48바이트 랜덤 → Base64 URL-safe 인코딩
        new SecureRandom().nextBytes(bytes); // SecureRandom: 암호학적으로 안전한 난수
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); // withoutPadding(): 쿠키/URL에서 다루기 편한 형태
    }
    
    // 회원 가입
    @Override
    public void signup(SignupRequestDto signupRequestDto) {
        // 이메일 중복 체크 (DB unique + 서비스 체크 이중 방어)
        int count = authMapper.countByEmail(signupRequestDto.getEmail());
        if (count > 0) throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");

        // 비밀번호를 hash로 변환 후 저장 (평문 저장 방지)
        signupRequestDto.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));

        authMapper.insertDesigner(signupRequestDto); // designers insert
    }

    // 로그인 + 토큰 발급(access + refresh)
    @Override
    @Transactional(readOnly = true) // 현재 코드상 refresh insert가 있는데 "readOnly=true"면 DB 설정에 따라 문제 가능
    public AuthIssueResult loginAndIssue(LoginRequestDto loginRequestDto) {

        // 로그인용 조회(비밀번호 포함)
        DesignerAuthDto authDesigner = authMapper.findAuthByEmail(loginRequestDto.getEmail());
        if (authDesigner == null) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 비밀번호 검증 (평문 vs 해시)
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), authDesigner.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // Access Token 발급(JWT)
        String accessToken = jwtTokenProvider.createToken(authDesigner.getId(), authDesigner.getEmail());

        // Refresh (선택: 단일 세션 정책이면 revokeAll)
        // refreshTokenMapper.revokeAllByDesignerId(authDesigner.getId());

        // Refresh Token 발급 + 저장
        String refreshValue = newRefreshTokenValue();

        RefreshTokenDto rt = new RefreshTokenDto();
        rt.setDesignerId(authDesigner.getId());
        rt.setToken(refreshValue);
        rt.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_DAYS));
        rt.setRevoked(false);

        // DB 저장
        refreshTokenMapper.insert(rt);

        // 응답용(비밀번호 제외) 사용자 정보 조회
        DesignerDto designerDto = authMapper.findById(authDesigner.getId());

        // Response Body 구성(access + designer)
        AuthResponseDto body = new AuthResponseDto();
        body.setAccessToken(accessToken);
        body.setDesigner(designerDto);

        // refreshToken은 Controller가 쿠키로 굽기 위해 따로 전달
        return new AuthIssueResult(body, refreshValue);
    }

    // refresh token 기반 재발급

    // 1. DB에서 refresh token이 유효한지 확인(revoked=0, not expired)
    // 2. rotation: 기존 refresh 폐기
    // 3.  새 refresh 발급/저장
    // 4. 새 access 발급
    // 5. 결과(body + newRefresh) 반환
    @Override
    public AuthIssueResult refreshAndIssue(String refreshToken) {

        // DB에서 유효한 refresh token 조회
        RefreshTokenDto saved = refreshTokenMapper.findValidByToken(refreshToken);
        if (saved == null) throw new IllegalArgumentException("Refresh token이 유효하지 않습니다.");

        Long designerId = saved.getDesignerId();

        // rotation: 기존 refresh 폐기
        refreshTokenMapper.revokeByToken(refreshToken);

        // 새 refresh 발급/저장
        String newRefresh = newRefreshTokenValue();
        
        RefreshTokenDto rt = new RefreshTokenDto();
        rt.setDesignerId(designerId);
        rt.setToken(newRefresh);
        rt.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_DAYS));
        rt.setRevoked(false);
        refreshTokenMapper.insert(rt);

        // 새 access 발급
        DesignerDto me = authMapper.findById(designerId);
        String newAccess = jwtTokenProvider.createToken(me.getId(), me.getEmail());

        // Response Body 구성
        AuthResponseDto body = new AuthResponseDto();
        body.setAccessToken(newAccess);
        body.setDesigner(me);

        return new AuthIssueResult(body, newRefresh);
    }

    // Logout : refresh token이 있으면 revoked 처리. access token은 stateless라 서버에서 "삭제" 개념이 없음.
    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        refreshTokenMapper.revokeByToken(refreshToken);
    }

    // 내 정보 조회 : JWT 필터가 request attribute로 넣어준 designerId 기반 조회
    @Override
    @Transactional(readOnly = true)
    public DesignerDto getMe(Long designerId) {
        return authMapper.findById(designerId);
    }

    // 회원 탈퇴 : designers 삭제. 해당 디자이너 refresh token 전체 폐기
    @Override
    public void deleteMe(Long designerId) {
        authMapper.deleteById(designerId);
        refreshTokenMapper.revokeAllByDesignerId(designerId);
    }
}
