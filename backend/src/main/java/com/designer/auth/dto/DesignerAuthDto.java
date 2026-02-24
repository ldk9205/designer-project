package com.designer.auth.dto;

import lombok.Getter;
import lombok.Setter;

// # 로그인 검증(인증)용 내부 DTO

// - DB에서 email로 user를 찾을 때(password hash 포함) 조회 결과로 사용
// - Service에서 PasswordEncoder.matches(raw, encoded) 검증에 활용
// - 보안상 절대 API 응답(front)으로 노출하면 안 된다.

@Getter
@Setter
public class DesignerAuthDto {
    private Long id; // 디자이너 PK
    private String email; // 이메일
    private String password; // 비밀번호 hash. 로그인 검증용. DB에 저장된 암호화/hash 값. 로그인 시 matches로 비교 대상
    private String name; // 이름( 로그인 응답에 쓰일 수 있는 최소 프로필 정보)
}
