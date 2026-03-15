package com.designer.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCreateRequestDto {

    // DB insert 후 MyBatis가 generated key를 넣어줄 용도
    // 클라이언트가 보내는 값으로 쓰는 목적은 아님
    private Long id;

    // 고객 이름
    @NotBlank(message = "name은 비어 있을 수 없습니다.")
    @Size(max = 50)
    private String name;

    // 고객 연락처
    @NotBlank
    @Size(max = 20)
    private String phone;

    // 고객 메모(선택 입력)
    private String memo;
}