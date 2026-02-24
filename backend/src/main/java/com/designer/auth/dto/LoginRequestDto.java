package com.designer.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// # 로그인 요청 DTO

// - /auth/login API에서 사용
// - @Valid 를 통해 controller level에서 입력값 검증 -> 이메일 형식/필수값, 비밀번호 필수값을 보장

@Getter
@Setter
public class LoginRequestDto {
    
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.") // 빈 문자열, 공백 불가
    private String email; // 로그인에 사용할 email (null, 빈 문자열, 공백 불가)
    
    @NotBlank(message = "비밀번호는 필수입니다.") // 빈 문자열, 공백 불가
    private String password; // 로그인 비밀번호(평문), server에서 DB의 hash와 PasswordEncoder.matches로 비교
}
