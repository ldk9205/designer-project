package com.designer.customer.service;

import com.designer.customer.dto.CustomerCreateRequestDto;
import com.designer.customer.dto.CustomerUpdateRequestDto;
import com.designer.customer.dto.CustomerResponseDto;

import java.util.List;

public interface CustomerService {

    // 고객 등록
    CustomerResponseDto createCustomer(Long designerId, CustomerCreateRequestDto dto);

    // 내 고객 목록 조회 + 이름 검색
    List<CustomerResponseDto> getCustomers(Long designerId, String name);

    // 내 고객 단건 조회
    CustomerResponseDto getCustomer(Long designerId, Long customerId);

    // 고객 수정
    CustomerResponseDto updateCustomer(Long designerId, Long customerId, CustomerUpdateRequestDto dto);

    // 고객 삭제
    void deleteCustomer(Long designerId, Long customerId);
}