package com.designer.customer.service;

import com.designer.customer.dto.CustomerCreateRequestDto;
import com.designer.customer.dto.CustomerUpdateRequestDto;
import com.designer.customer.dto.CustomerResponseDto;
import com.designer.customer.exception.CustomerNotFoundException;
import com.designer.customer.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.designer.s3.service.S3Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final S3Service s3Service;

    // 고객 등록
    // 1. insert 수행
    // 2. generated key가 dto.id에 세팅됨
    // 3. 방금 생성된 고객을 다시 조회해서 응답 DTO 반환
    @Override
    public CustomerResponseDto createCustomer(Long designerId, CustomerCreateRequestDto dto) {
        customerMapper.insertCustomer(designerId, dto);

        CustomerResponseDto created =
                customerMapper.selectCustomerByIdAndDesignerId(dto.getId(), designerId);

        if (created == null) {
            throw new CustomerNotFoundException(dto.getId());
        }

        return created;
    }

    // 내 고객 목록 조회 + 이름 검색
    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getCustomers(Long designerId, String name) {
        String keyword = (name == null) ? null : name.trim();

        // 검색어가 null 또는 공백뿐이면 전체 목록 조회로 처리
        if (keyword != null && keyword.isEmpty()) {
            keyword = null;
        }

        return customerMapper.selectCustomersByDesignerIdAndName(designerId, keyword);
    }

    // 내 고객 단건 조회
    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomer(Long designerId, Long customerId) {
        CustomerResponseDto customer =
                customerMapper.selectCustomerByIdAndDesignerId(customerId, designerId);

        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }

        return customer;
    }

    // 고객 수정
    @Override
    public CustomerResponseDto updateCustomer(Long designerId, Long customerId, CustomerUpdateRequestDto dto) {

        if (dto.getName() == null && dto.getPhone() == null && dto.getMemo() == null) {
            throw new IllegalArgumentException("수정할 값이 없습니다.");
        }

        if (dto.getName() != null && dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("name은 비어 있을 수 없습니다.");
        }

        if (dto.getPhone() != null && dto.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("phone은 비어 있을 수 없습니다.");
        }

        int updated = customerMapper.updateCustomerByIdAndDesignerId(customerId, designerId, dto);

        if (updated == 0) {
            throw new CustomerNotFoundException(customerId);
        }

        return getCustomer(designerId, customerId);
    }

    // 고객 삭제
    @Override
    public void deleteCustomer(Long designerId, Long customerId) {
        CustomerResponseDto customer =
                customerMapper.selectCustomerByIdAndDesignerId(customerId, designerId);

        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }

        String customerPrefix = customerId + "/";

        s3Service.deleteObjectsByPrefix(customerPrefix);

        int deleted =
                customerMapper.deleteCustomerByIdAndDesignerId(customerId, designerId);

        if (deleted == 0) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}