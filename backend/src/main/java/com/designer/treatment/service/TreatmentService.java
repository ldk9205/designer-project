package com.designer.treatment.service;

import com.designer.treatment.dto.*;

public interface TreatmentService {

    TreatmentCreateResponseDto createTreatment(Long designerId, TreatmentCreateRequestDto dto);

    TreatmentPageResponseDto getTreatmentsByCustomer(Long designerId, Long customerId, int page, int size, String category, String sortDirection);

    TreatmentDetailResponseDto getTreatmentDetail(Long designerId, Long treatmentId);

    void updateTreatment(Long designerId, Long treatmentId, TreatmentUpdateRequestDto dto);

    void deleteTreatment(Long designerId, Long treatmentId);
}