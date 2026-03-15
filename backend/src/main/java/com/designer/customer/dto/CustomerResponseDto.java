package com.designer.customer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerResponseDto {
    // 고객 PK
    private Long id;

    // 소유 디자이너 PK
    private Long designerId;
    
    // 고객 이름
    private String name;
    
    // 고객 연락처
    private String phone;
    
    // 고객 메모
    private String memo;
    
    // 생성 시각
    private LocalDateTime createdAt;
    
    // 수정 시각
    private LocalDateTime updatedAt;
}