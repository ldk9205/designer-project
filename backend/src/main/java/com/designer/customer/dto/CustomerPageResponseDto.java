package com.designer.customer.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CustomerPageResponseDto {

    private List<CustomerResponseDto> content; // 현재 페이지 데이터
    private int page;                          // 현재 페이지 (0-based)
    private int size;                          // 페이지 크기
    private long totalElements;                // 전체 데이터 수
    private int totalPages;                    // 전체 페이지 수
    private boolean first;                     // 첫 페이지 여부
    private boolean last;                      // 마지막 페이지 여부
}