package com.designer.treatment.controller;

import com.designer.treatment.dto.TreatmentCreateRequestDto;
import com.designer.treatment.dto.TreatmentDetailResponseDto;
import com.designer.treatment.dto.TreatmentResponseDto;
import com.designer.treatment.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping("/treatments")
    public ResponseEntity<?> createTreatment(
            @Valid @RequestBody TreatmentCreateRequestDto request,
            @RequestAttribute("designerId") Long designerId
    ) {
        treatmentService.createTreatment(request, designerId);
        return ResponseEntity.ok("시술 등록 완료");
    }

    @GetMapping("/customers/{id}/treatments")
    public ResponseEntity<List<TreatmentResponseDto>> getTreatmentsByCustomer(
            @PathVariable("id") Long customerId,
            @RequestAttribute("designerId") Long designerId
    ) {
        return ResponseEntity.ok(
                treatmentService.getTreatmentsByCustomer(customerId, designerId)
        );
    }

    @GetMapping("/treatments/{id}")
    public ResponseEntity<TreatmentDetailResponseDto> getTreatmentDetail(
            @PathVariable("id") Long treatmentId,
            @RequestAttribute("designerId") Long designerId
    ) {
        return ResponseEntity.ok(
                treatmentService.getTreatmentDetail(treatmentId, designerId)
        );
    }

    @DeleteMapping("/treatments/{id}")
    public ResponseEntity<?> deleteTreatment(
            @PathVariable("id") Long treatmentId,
            @RequestAttribute("designerId") Long designerId
    ) {
        treatmentService.deleteTreatment(treatmentId, designerId);
        return ResponseEntity.ok("시술 삭제 완료");
    }
}