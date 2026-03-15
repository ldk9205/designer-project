package com.designer.customer.mapper;

import com.designer.customer.dto.CustomerCreateRequestDto;
import com.designer.customer.dto.CustomerUpdateRequestDto;
import com.designer.customer.dto.CustomerResponseDto;
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

    // 내 고객 목록 조회
    List<CustomerResponseDto> selectCustomersByDesignerId(@Param("designerId") Long designerId);

    // 고객 부분 수정. 수정된 row 수 반환
    int updateCustomerByIdAndDesignerId(@Param("customerId") Long customerId,
                                        @Param("designerId") Long designerId,
                                        @Param("dto") CustomerUpdateRequestDto dto);

    // 고객 삭제. 삭제된 row 수 반환
    int deleteCustomerByIdAndDesignerId(@Param("customerId") Long customerId,
                                        @Param("designerId") Long designerId);
}