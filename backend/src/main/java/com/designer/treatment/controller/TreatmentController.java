package com.designer.treatment.controller;

import com.designer.treatment.dto.TreatmentCreateRequestDto;
import com.designer.treatment.dto.TreatmentCreateResponseDto;
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

    @PostMapping("/treatments")
    public ResponseEntity<TreatmentCreateResponseDto> create(
            @RequestAttribute("designerId") Long designerId,
            @Valid @RequestBody TreatmentCreateRequestDto dto
    ) {
        TreatmentCreateResponseDto response = treatmentService.createTreatment(designerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/customers/{customerId}/treatments")
    public ResponseEntity<List<TreatmentResponseDto>> list(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(
                treatmentService.getTreatmentsByCustomer(designerId, customerId)
        );
    }

    @GetMapping("/treatments/{treatmentId}")
    public ResponseEntity<TreatmentDetailResponseDto> get(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long treatmentId
    ) {
        return ResponseEntity.ok(
                treatmentService.getTreatmentDetail(designerId, treatmentId)
        );
    }

    @PatchMapping("/treatments/{treatmentId}")
    public ResponseEntity<Void> update(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long treatmentId,
            @Valid @RequestBody TreatmentUpdateRequestDto dto
    ) {
        treatmentService.updateTreatment(designerId, treatmentId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/treatments/{treatmentId}")
    public ResponseEntity<Void> delete(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long treatmentId
    ) {
        treatmentService.deleteTreatment(designerId, treatmentId);
        return ResponseEntity.noContent().build();
    }
}