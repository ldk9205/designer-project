package com.designer.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Refresh Token 저장/관리용 DTO(refresh_token table row)

// - 로그인 시 refresh token 발급 후 DB 저장에 사용
// - /auth/refresh에서 유효성 확인(findValidByToken( 및 rotation(revoke + insert)에 사용
// - /auth/logout에서 revoke에 사용

// 설계 의도 : access token은 steteless(JWT), refresh token은 stateful(DB 저장)로 운영해서 logout,회수, 만료 관리 가능

@Getter
@Setter
public class RefreshTokenDto {
    private Long id; // refresh_tokens PK
    private Long designerId; // 어떤 designer의 refresh token 인지
    private String token; // refresh token 원문(random 문자열)
    private LocalDateTime expiresAt; // 만료 시각
    private boolean revoked; // 폐기 여부(logout, rotation 등으로 revoked 처리)
    private LocalDateTime createdAt; // 생성 시각
    private LocalDateTime updatedAt; // upodate 시각(폐기 처리 등)
}
