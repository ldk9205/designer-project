package com.designer.treatment.service;

import com.designer.image.dto.ImageResponseDto;
import com.designer.image.service.ImageService;
import com.designer.treatment.dto.TreatmentCreateRequestDto;
import com.designer.treatment.dto.TreatmentDetailResponseDto;
import com.designer.treatment.dto.TreatmentDto;
import com.designer.treatment.dto.TreatmentResponseDto;
import com.designer.treatment.dto.TreatmentUpdateRequestDto;
import com.designer.treatment.mapper.TreatmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentMapper treatmentMapper;
    private final ImageService imageService;

    @Override
    @Transactional
    public void createTreatment(Long designerId, TreatmentCreateRequestDto dto) {
        TreatmentDto treatment = new TreatmentDto();
        treatment.setDesignerId(designerId);
        treatment.setCustomerId(dto.getCustomerId());
        treatment.setTreatmentDate(dto.getTreatmentDate());
        treatment.setTreatmentTime(dto.getTreatmentTime());
        treatment.setCategory(dto.getCategory());
        treatment.setStyleName(dto.getStyleName());
        treatment.setDetail(dto.getDetail());

        treatmentMapper.insertTreatment(treatment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponseDto> getTreatmentsByCustomer(Long designerId, Long customerId) {
        List<TreatmentDto> treatments =
                treatmentMapper.selectTreatmentsByCustomerIdAndDesignerId(customerId, designerId);

        return treatments.stream()
                .map(t -> TreatmentResponseDto.builder()
                        .id(t.getId())
                        .treatmentDate(t.getTreatmentDate())
                        .treatmentTime(t.getTreatmentTime())
                        .category(t.getCategory())
                        .styleName(t.getStyleName())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentDetailResponseDto getTreatmentDetail(Long designerId, Long treatmentId) {
        TreatmentDto treatment =
                treatmentMapper.selectTreatmentByIdAndDesignerId(treatmentId, designerId);

        if (treatment == null) {
            throw new IllegalArgumentException("해당 시술 이력을 찾을 수 없습니다.");
        }

        List<ImageResponseDto> images =
                imageService.getImagesByTreatmentId(treatmentId, designerId);

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

    @Override
    @Transactional
    public void updateTreatment(Long designerId, Long treatmentId, TreatmentUpdateRequestDto dto) {
        TreatmentDto existing =
                treatmentMapper.selectTreatmentByIdAndDesignerId(treatmentId, designerId);

        if (existing == null) {
            throw new IllegalArgumentException("해당 시술 이력을 찾을 수 없습니다.");
        }

        boolean hasNoChanges =
                dto.getTreatmentDate() == null &&
                        dto.getTreatmentTime() == null &&
                        dto.getCategory() == null &&
                        dto.getStyleName() == null &&
                        dto.getDetail() == null;

        if (hasNoChanges) {
            throw new IllegalArgumentException("수정할 내용이 없습니다.");
        }

        int updated = treatmentMapper.updateTreatmentByIdAndDesignerId(treatmentId, designerId, dto);

        if (updated == 0) {
            throw new IllegalStateException("시술 이력 수정에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteTreatment(Long designerId, Long treatmentId) {
        int deleted = treatmentMapper.deleteTreatmentByIdAndDesignerId(treatmentId, designerId);

        if (deleted == 0) {
            throw new IllegalArgumentException("해당 시술 이력을 찾을 수 없습니다.");
        }
    }
}