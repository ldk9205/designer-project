package com.designer.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DesignerUpdateRequestDto {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다.")
    private String name;

    @Pattern(
            regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$",
            message = "휴대폰 번호 형식이 올바르지 않습니다."
    )
    private String phone;
}