package com.designer.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDto {
    private String accessToken; // 이 사용자가 이미 로그인에 성공한 디자이너임을 증명하는 신분증
    private DesignerDto designer;
}
