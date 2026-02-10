package com.designer.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DesignerAuthDto {
    private Long id;
    private String email;
    private String password; // 🔐 로그인 검증용
    private String name;
}
