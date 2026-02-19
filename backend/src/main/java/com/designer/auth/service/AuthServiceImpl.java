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

    private final AuthMapper authMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private static final long REFRESH_DAYS = 14;

    private String newRefreshTokenValue() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public void signup(SignupRequestDto signupRequestDto) {
        int count = authMapper.countByEmail(signupRequestDto.getEmail());
        if (count > 0) throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");

        signupRequestDto.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        authMapper.insertDesigner(signupRequestDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthIssueResult loginAndIssue(LoginRequestDto loginRequestDto) {
        DesignerAuthDto authDesigner = authMapper.findAuthByEmail(loginRequestDto.getEmail());
        if (authDesigner == null) throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), authDesigner.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // Access
        String accessToken = jwtTokenProvider.createToken(authDesigner.getId(), authDesigner.getEmail());

        // Refresh (선택: 단일 세션 정책이면 revokeAll)
        // refreshTokenMapper.revokeAllByDesignerId(authDesigner.getId());

        String refreshValue = newRefreshTokenValue();
        RefreshTokenDto rt = new RefreshTokenDto();
        rt.setDesignerId(authDesigner.getId());
        rt.setToken(refreshValue);
        rt.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_DAYS));
        rt.setRevoked(false);
        refreshTokenMapper.insert(rt);

        DesignerDto designerDto = authMapper.findById(authDesigner.getId());

        AuthResponseDto body = new AuthResponseDto();
        body.setAccessToken(accessToken);
        body.setDesigner(designerDto);

        return new AuthIssueResult(body, refreshValue);
    }

    @Override
    public AuthIssueResult refreshAndIssue(String refreshToken) {
        RefreshTokenDto saved = refreshTokenMapper.findValidByToken(refreshToken);
        if (saved == null) throw new IllegalArgumentException("Refresh token이 유효하지 않습니다.");

        Long designerId = saved.getDesignerId();

        // ✅ rotation: 기존 refresh 폐기
        refreshTokenMapper.revokeByToken(refreshToken);

        // ✅ 새 refresh 발급/저장
        String newRefresh = newRefreshTokenValue();
        RefreshTokenDto rt = new RefreshTokenDto();
        rt.setDesignerId(designerId);
        rt.setToken(newRefresh);
        rt.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_DAYS));
        rt.setRevoked(false);
        refreshTokenMapper.insert(rt);

        // ✅ 새 access 발급
        DesignerDto me = authMapper.findById(designerId);
        String newAccess = jwtTokenProvider.createToken(me.getId(), me.getEmail());

        AuthResponseDto body = new AuthResponseDto();
        body.setAccessToken(newAccess);
        body.setDesigner(me);

        return new AuthIssueResult(body, newRefresh);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        refreshTokenMapper.revokeByToken(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public DesignerDto getMe(Long designerId) {
        return authMapper.findById(designerId);
    }

    @Override
    public void deleteMe(Long designerId) {
        authMapper.deleteById(designerId);
        refreshTokenMapper.revokeAllByDesignerId(designerId);
    }
}
