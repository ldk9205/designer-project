package com.designer.customer.mapper;

import com.designer.customer.dto.CustomerCreateRequestDto;
import com.designer.customer.dto.CustomerResponseDto;
import com.designer.customer.dto.CustomerUpdateRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {

    // 고객 등록
    void insertCustomer(@Param("designerId") Long designerId,
                        @Param("dto") CustomerCreateRequestDto dto);

    // 내 고객 단일 조회
    CustomerResponseDto selectCustomerByIdAndDesignerId(@Param("customerId") Long customerId,
                                                        @Param("designerId") Long designerId);

    // 내 고객 목록 조회 + 이름 검색 + Paging Block
    List<CustomerResponseDto> selectCustomersByDesignerIdAndNamePaged(
            @Param("designerId") Long designerId,
            @Param("name") String name,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // 내 고객 수 조회 + 이름 검색
    long countCustomersByDesignerIdAndName(
            @Param("designerId") Long designerId,
            @Param("name") String name
    );

    // 고객 부분 수정
    int updateCustomerByIdAndDesignerId(@Param("customerId") Long customerId,
                                        @Param("designerId") Long designerId,
                                        @Param("dto") CustomerUpdateRequestDto dto);

    // 고객 삭제
    int deleteCustomerByIdAndDesignerId(@Param("customerId") Long customerId,
                                        @Param("designerId") Long designerId);
}