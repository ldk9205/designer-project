package com.designer.customer.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerUpdateRequestDto {

    @Size(max = 50, message = "name은 50자 이하여야 합니다.")
    private String name;

    @Size(max = 20, message = "phone은 20자 이하여야 합니다.")
    private String phone;

    private String memo;
}