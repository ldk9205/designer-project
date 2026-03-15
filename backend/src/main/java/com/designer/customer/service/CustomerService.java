package com.designer.customer.service;

import com.designer.customer.dto.CustomerCreateRequestDto;
import com.designer.customer.dto.CustomerUpdateRequestDto;
import com.designer.customer.dto.CustomerResponseDto;

import java.util.List;

public interface CustomerService {

    CustomerResponseDto createCustomer(Long designerId, CustomerCreateRequestDto dto);

    List<CustomerResponseDto> getCustomers(Long designerId);

    CustomerResponseDto getCustomer(Long designerId, Long customerId);

    CustomerResponseDto updateCustomer(Long designerId, Long customerId, CustomerUpdateRequestDto dto);

    void deleteCustomer(Long designerId, Long customerId);
}