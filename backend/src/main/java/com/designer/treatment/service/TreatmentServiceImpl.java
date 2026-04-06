package com.designer.treatment.service;

import com.designer.image.dto.ImageResponseDto;
import com.designer.image.service.ImageService;
import com.designer.s3.service.S3Service;
import com.designer.treatment.dto.*;
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
    private final S3Service s3Service;

    @Override
    @Transactional
    public TreatmentCreateResponseDto createTreatment(Long designerId, TreatmentCreateRequestDto dto) {
        TreatmentDto treatment = new TreatmentDto();
        treatment.setDesignerId(designerId);
        treatment.setCustomerId(dto.getCustomerId());
        treatment.setTreatmentDate(dto.getTreatmentDate());
        treatment.setTreatmentTime(dto.getTreatmentTime());
        treatment.setCategory(dto.getCategory());
        treatment.setStyleName(dto.getStyleName());
        treatment.setDetail(dto.getDetail());

        treatmentMapper.insertTreatment(treatment);

        return new TreatmentCreateResponseDto(treatment.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentPageResponseDto getTreatmentsByCustomer(Long designerId, Long customerId, int page, int size, String category, String sortDirection) {
        if (page < 0) {page = 0;}
        if (size <= 0) {size = 5;}
        if (size > 100) {size = 100;}

        String normalizedCategory = category == null ? null : category.trim();
        if (normalizedCategory != null && normalizedCategory.isEmpty()) {
            normalizedCategory = null;
        }

        String normalizedSortDirection =
                "asc".equalsIgnoreCase(sortDirection) ? "asc" : "desc";

        int offset = page * size;

        long totalElements = treatmentMapper.countTreatmentsByCustomerIdAndDesignerId(
                customerId,
                designerId,
                normalizedCategory
        );

        List<TreatmentDto> treatments =
                treatmentMapper.selectTreatmentsByCustomerIdAndDesignerId(
                        customerId,
                        designerId,
                        normalizedCategory,
                        normalizedSortDirection,
                        offset,
                        size
                );

        List<TreatmentResponseDto> content = treatments.stream()
                .map(t -> TreatmentResponseDto.builder()
                        .id(t.getId())
                        .treatmentDate(t.getTreatmentDate())
                        .treatmentTime(t.getTreatmentTime())
                        .category(t.getCategory())
                        .styleName(t.getStyleName())
                        .build())
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return TreatmentPageResponseDto.builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(totalPages == 0 || page >= totalPages - 1)
                .build();
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
        TreatmentDto treatment =
                treatmentMapper.selectTreatmentByIdAndDesignerId(treatmentId, designerId);

        if (treatment == null) {
            throw new IllegalArgumentException("해당 시술 이력을 찾을 수 없습니다.");
        }

        String treatmentPrefix = treatment.getCustomerId() + "/" + treatmentId + "/";
        s3Service.deleteObjectsByPrefix(treatmentPrefix);

        int deleted = treatmentMapper.deleteTreatmentByIdAndDesignerId(treatmentId, designerId);

        if (deleted == 0) {
            throw new IllegalStateException("시술 이력 삭제에 실패했습니다.");
        }
    }
}