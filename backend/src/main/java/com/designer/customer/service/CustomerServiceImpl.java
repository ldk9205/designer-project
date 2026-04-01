package com.designer.customer.service;

import com.designer.customer.dto.CustomerCreateRequestDto;
import com.designer.customer.dto.CustomerPageResponseDto;
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

    // 내 고객 목록 조회 + 이름 검색 + Paging Block
    @Override
    @Transactional(readOnly = true)
    public CustomerPageResponseDto getCustomers(Long designerId, String name, int page, int size) {
        String keyword = (name == null) ? null : name.trim();

        if (keyword != null && keyword.isEmpty()) {
            keyword = null;
        }

        if (page < 0) {
            throw new IllegalArgumentException("page는 0 이상이어야 합니다.");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("size는 1 이상이어야 합니다.");
        }

        long totalElements = customerMapper.countCustomersByDesignerIdAndName(designerId, keyword);
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);

        if (totalPages > 0 && page >= totalPages) {
            throw new IllegalArgumentException("존재하지 않는 페이지입니다.");
        }

        int offset = page * size;

        List<CustomerResponseDto> content =
                customerMapper.selectCustomersByDesignerIdAndNamePaged(designerId, keyword, size, offset);

        return CustomerPageResponseDto.builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(totalPages == 0 || page >= totalPages - 1)
                .build();
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