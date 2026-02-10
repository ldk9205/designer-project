package com.designer.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    /*
     * 로그인에 사용할 이메일
     * - 이메일 형식인지 검증
     * - null / 빈 문자열 / 공백 불가
     */
    // 값이 이메일 형식인지 검사. 내부적으로 RFC 표준 기반의 정규식 사용
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    // 값이 null, "", " " (공백만) ❌. 문자열 필수값 검증에 가장 많이 사용.
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    /*
     * 로그인 비밀번호 (평문)
     * - DB에는 암호화해서 저장됨
     * - 길이 제한으로 보안 최소 기준 설정
     */
    @NotBlank(message = "비밀번호는 필수입니다.")
    // 문자열의 길이(length) 제한. 컬렉션(List, Set)에도 사용 가능
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
    private String password;

    /*
     * 디자이너 이름
     * - 서비스 내에서 표시되는 실명 또는 닉네임
     */
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다.")
    private String name;

    // 정규식 기반 검증
    @Pattern(
            /*
             * 01[0-9]        → 010, 011, 016, 017, 018, 019
             * -?             → 하이픈 있어도 되고 없어도 됨
             * [0-9]{3,4}     → 중간 자리 3~4자리
             * [0-9]{4}       → 마지막 4자리
             */
            regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$",
            message = "휴대폰 번호 형식이 올바르지 않습니다."
    )
    private String phone;
}
