package com.designer.treatment.controller;

import com.designer.treatment.dto.*;
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
    public ResponseEntity<TreatmentPageResponseDto> list(
            @RequestAttribute("designerId") Long designerId,
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ResponseEntity.ok(
                treatmentService.getTreatmentsByCustomer(
                        designerId,
                        customerId,
                        page,
                        size,
                        category,
                        sortDirection
                )
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