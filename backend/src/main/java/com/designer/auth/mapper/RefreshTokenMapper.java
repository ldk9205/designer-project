package com.designer.auth.mapper;

import com.designer.auth.dto.RefreshTokenDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {

    // refresh token 저장 : 로그인 성공시, rotation 시
    void insert(RefreshTokenDto dto); 
    
    // 유효한 refresh token 조회 : revoked = 0, expires_at > NOW()
    RefreshTokenDto findValidByToken(@Param("token") String token);
    
   // 특정 refresh token 폐기 : logout, rotation
    void revokeByToken(@Param("token") String token);
    
    // 특정 디자이너의 모든 refresh token 폐기 : 단일 session 정책, 회원 탈퇴 
    void revokeAllByDesignerId(@Param("designerId") Long designerId);
}