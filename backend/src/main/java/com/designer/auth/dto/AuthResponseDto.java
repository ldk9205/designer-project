package com.designer.auth.dto;

import lombok.Getter;
import lombok.Setter;

// # 인증 성공 응답 DTO (Response Body)

// - /auth/login, /auth/refresh 성공 시 return
// - 구성 : accessToken, designer
// - 참고 : refreshToken은 보안상 HttpOnly cookie로 내려주고, body에는 포함하지 ㅇ낳도록 설계

@Getter
@Setter
public class AuthResponseDto {
    private String accessToken; // API 호출에 사용할 JWT. 이 사용자가 이미 로그인에 성공한 디자이너임을 증명하는 신분증
    private DesignerDto designer; // login된 designer 프로필(외부 노출용)
}
