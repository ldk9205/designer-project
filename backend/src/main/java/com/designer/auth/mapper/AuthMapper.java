package com.designer.auth.mapper;

import com.designer.auth.dto.DesignerDto;
import com.designer.auth.dto.DesignerAuthDto;
import com.designer.auth.dto.SignupRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// designers 테이블에 대한 DB 접근 인터페이스
// 로그인 / 회원가입 / 내 정보 조회 / 회원 탈퇴 처리
@Mapper
public interface AuthMapper {
    // 회원 가입 : designers 테이블에 디자이너 계정 생성
    int insertDesigner(SignupRequestDto signupRequestDto);

    // 이메일로 디자이너 조회 (로그인용), password 비교는 Service에서 수행
    DesignerAuthDto findAuthByEmail(@Param("email") String email);

    // 디자이너 ID로 내 정보 조회 (/auth/me)
    DesignerDto findById(@Param("id") Long id);

    // 회원 탈퇴 (디자이너 계정 삭제)
    int deleteById(@Param("id") Long id);

    // 회원가입용: 이메일 중복 체크
    int countByEmail(@Param("email") String email);
}
