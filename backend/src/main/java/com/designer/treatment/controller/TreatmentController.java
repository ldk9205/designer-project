package com.designer.treatment.controller;

import com.designer.treatment.dto.TreatmentCreateRequestDto;
import com.designer.treatment.dto.TreatmentDetailResponseDto;
import com.designer.treatment.dto.TreatmentResponseDto;
import com.designer.treatment.dto.TreatmentUpdateRequestDto;
import com.designer.treatment.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;

    // 시술 등록
    @PostMapping("/treatments")
    public ResponseEntity<Void> create(
            @RequestAttribute("designerId") Long designerId,
            @Valid @RequestBody TreatmentCreateRequestDto dto
    ) {
        treatmentService.createTreatment(designerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 특정 고객의 시술 이력 목록 조회
    @GetMapping("/customers/{customerId}/treatments")
    public ResponseEntity<List<TreatmentResponseDto>> list(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(
                treatmentService.getTreatmentsByCustomer(designerId, customerId)
        );
    }

    // 시술 상세 조회
    @GetMapping("/treatments/{treatmentId}")
    public ResponseEntity<TreatmentDetailResponseDto> get(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long treatmentId
    ) {
        return ResponseEntity.ok(
                treatmentService.getTreatmentDetail(designerId, treatmentId)
        );
    }

    // 시술 부분 수정
    @PatchMapping("/treatments/{treatmentId}")
    public ResponseEntity<Void> update(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long treatmentId,
            @Valid @RequestBody TreatmentUpdateRequestDto dto
    ) {
        treatmentService.updateTreatment(designerId, treatmentId, dto);
        return ResponseEntity.noContent().build();
    }

    // 시술 삭제
    @DeleteMapping("/treatments/{treatmentId}")
    public ResponseEntity<Void> delete(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long treatmentId
    ) {
        treatmentService.deleteTreatment(designerId, treatmentId);
        return ResponseEntity.noContent().build();
    }
}