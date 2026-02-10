package com.designer.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    /**
     * 로그인에 사용할 이메일
     * - 이메일 형식인지 검증
     * - null / 빈 문자열 / 공백 불가
     */
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
