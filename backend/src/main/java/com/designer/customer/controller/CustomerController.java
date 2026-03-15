package com.designer.customer.controller;

import com.designer.customer.dto.CustomerCreateRequestDto;
import com.designer.customer.dto.CustomerUpdateRequestDto;
import com.designer.customer.dto.CustomerResponseDto;
import com.designer.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // 고객 등록
    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(
            @RequestAttribute("designerId") Long designerId,
            @Valid @RequestBody CustomerCreateRequestDto dto
    ) {
        CustomerResponseDto created = customerService.createCustomer(designerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 내 고객 목록 조회
    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> list(
            @RequestAttribute("designerId") Long designerId
    ) {
        return ResponseEntity.ok(customerService.getCustomers(designerId));
    }

    // 내 고객 단일 조회
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDto> get(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(customerService.getCustomer(designerId, customerId));
    }

    // 고객 부분 수정
    @PatchMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDto> update(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(customerService.updateCustomer(designerId, customerId, dto));
    }

    // 고객 삭제
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> delete(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long customerId
    ) {
        customerService.deleteCustomer(designerId, customerId);
        return ResponseEntity.noContent().build();
    }

}