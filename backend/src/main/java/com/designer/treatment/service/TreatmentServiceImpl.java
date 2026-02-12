package com.designer.treatment.service;

import com.designer.image.dto.ImageResponseDto;
import com.designer.image.service.ImageServiceImpl;
import com.designer.treatment.dto.*;
import com.designer.treatment.mapper.TreatmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentServiceImpl implements TreatmentService{

    private final TreatmentMapper treatmentMapper;
    private final ImageServiceImpl imageServiceImpl;

    // 🔹 시술 등록
    @Transactional
    public void createTreatment(
            TreatmentCreateRequestDto request,
            Long designerId
    ) {

        TreatmentDto treatment = new TreatmentDto();
        treatment.setDesignerId(designerId);
        treatment.setCustomerId(request.getCustomerId());
        treatment.setTreatmentDate(request.getTreatmentDate());
        treatment.setTreatmentTime(request.getTreatmentTime());
        treatment.setCategory(request.getCategory());
        treatment.setStyleName(request.getStyleName());
        treatment.setDetail(request.getDetail());

        treatmentMapper.insertTreatment(treatment);
    }

    // 🔹 고객별 목록
    @Transactional(readOnly = true)
    public List<TreatmentResponseDto> getTreatmentsByCustomer(
            Long customerId,
            Long designerId
    ) {

        List<TreatmentDto> treatments =
                treatmentMapper.findByCustomerId(customerId);

        return treatments.stream()
                .filter(t -> t.getDesignerId().equals(designerId))
                .map(t -> TreatmentResponseDto.builder()
                        .id(t.getId())
                        .treatmentDate(t.getTreatmentDate())
                        .treatmentTime(t.getTreatmentTime())
                        .category(t.getCategory())
                        .styleName(t.getStyleName())
                        .build()
                )
                .toList();
    }

    // 🔹 상세 조회 (+ image 포함)
    @Transactional(readOnly = true)
    public TreatmentDetailResponseDto getTreatmentDetail(
            Long treatmentId,
            Long designerId
    ) {

        TreatmentDto treatment =
                treatmentMapper.findById(treatmentId);

        if (treatment == null) {
            throw new IllegalArgumentException("존재하지 않는 시술입니다.");
        }

        if (!treatment.getDesignerId().equals(designerId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        List<ImageResponseDto> images =
                imageServiceImpl.getImagesByTreatmentId(
                        treatmentId,
                        designerId
                );

        return TreatmentDetailResponseDto.builder()
                .id(treatment.getId())
                .customerId(treatment.getCustomerId())
                .treatmentDate(treatment.getTreatmentDate())
                .treatmentTime(treatment.getTreatmentTime())
                .category(treatment.getCategory())
                .styleName(treatment.getStyleName())
                .detail(treatment.getDetail())
                .createdAt(treatment.getCreatedAt())
                .updatedAt(treatment.getUpdatedAt())
                .images(images)
                .build();
    }

    // 🔹 삭제
    @Transactional
    public void deleteTreatment(
            Long treatmentId,
            Long designerId
    ) {

        Long ownerDesignerId =
                treatmentMapper.findDesignerIdByTreatmentId(treatmentId);

        if (ownerDesignerId == null) {
            throw new IllegalArgumentException("존재하지 않는 시술입니다.");
        }

        if (!ownerDesignerId.equals(designerId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        treatmentMapper.deleteById(treatmentId);
    }
}
