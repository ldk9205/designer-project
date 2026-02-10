package com.designer.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DesignerDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private LocalDateTime createdAt;
}
