package com.designer.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthIssueResult {
    private AuthResponseDto body;
    private String refreshToken;
}
