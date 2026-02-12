//package com.designer.treatment.controller;
//
//import com.designer.auth.security.CustomUserDetails;
//import com.designer.treatment.dto.*;
//import com.designer.treatment.service.TreatmentServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class TreatmentController {
//
//    private final TreatmentServiceImpl treatmentServiceImpl;
//
//    // 🔹 시술 등록
//    @PostMapping("/treatments")
//    public ResponseEntity<?> createTreatment(
//            @RequestBody TreatmentCreateRequestDto request,
//            @AuthenticationPrincipal CustomUserDetails user
//    ) {
//
//        treatmentServiceImpl.createTreatment(
//                request,
//                user.getDesignerId()
//        );
//
//        return ResponseEntity.ok("시술 등록 완료");
//    }
//
//    // 🔹 고객별 시술 목록
//    @GetMapping("/customers/{id}/treatments")
//    public ResponseEntity<List<TreatmentResponseDto>> getTreatmentsByCustomer(
//            @PathVariable("id") Long customerId,
//            @AuthenticationPrincipal CustomUserDetails user
//    ) {
//
//        return ResponseEntity.ok(
//                treatmentServiceImpl.getTreatmentsByCustomer(
//                        customerId,
//                        user.getDesignerId()
//                )
//        );
//    }
//
//    // 🔹 시술 상세 조회 (+ image 포함)
//    @GetMapping("/treatments/{id}")
//    public ResponseEntity<TreatmentDetailResponseDto> getTreatmentDetail(
//            @PathVariable("id") Long treatmentId,
//            @AuthenticationPrincipal CustomUserDetails user
//    ) {
//
//        return ResponseEntity.ok(
//                treatmentServiceImpl.getTreatmentDetail(
//                        treatmentId,
//                        user.getDesignerId()
//                )
//        );
//    }
//
//    // 🔹 시술 삭제
//    @DeleteMapping("/treatments/{id}")
//    public ResponseEntity<?> deleteTreatment(
//            @PathVariable("id") Long treatmentId,
//            @AuthenticationPrincipal CustomUserDetails user
//    ) {
//
//        treatmentServiceImpl.deleteTreatment(
//                treatmentId,
//                user.getDesignerId()
//        );
//
//        return ResponseEntity.ok("시술 삭제 완료");
//    }
//}
