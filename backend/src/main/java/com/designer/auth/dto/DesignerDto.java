package com.designer.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


// 외부(front/client)로 노출 가능한 디자이너 기본 정보 DTO

// - 로그인 성공 응답(/auth.login), toekn 재발급 응답(/auth/refresh)에서 내려줌
// - 내 정보 조회(auth/me) 응답으로도 사용
// - 민감 정보(비밀번호 등)는 절대 포함하지 않음

@Getter
@Setter
public class DesignerDto {
    private Long id; // 디자이너 PK
    private String email; // 로그인에 사용하는 email. Unique
    private String name; // 디자이너 이름
    private String phone; // 연락처
    private LocalDateTime createdAt; // 계정 생성 시각
}
