package com.designer.auth.mapper;

import com.designer.auth.dto.RefreshTokenDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {

    void insert(RefreshTokenDto dto);

    RefreshTokenDto findValidByToken(@Param("token") String token);

    void revokeByToken(@Param("token") String token);

    void revokeAllByDesignerId(@Param("designerId") Long designerId);
}