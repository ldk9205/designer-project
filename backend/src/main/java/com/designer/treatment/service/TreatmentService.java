package com.designer.treatment.service;

import com.designer.treatment.dto.TreatmentCreateRequestDto;
import com.designer.treatment.dto.TreatmentDetailResponseDto;
import com.designer.treatment.dto.TreatmentResponseDto;
import com.designer.treatment.dto.TreatmentUpdateRequestDto;

import java.util.List;

public interface TreatmentService {

    void createTreatment(Long designerId, TreatmentCreateRequestDto dto);

    List<TreatmentResponseDto> getTreatmentsByCustomer(Long designerId, Long customerId);

    TreatmentDetailResponseDto getTreatmentDetail(Long designerId, Long treatmentId);

    void updateTreatment(Long designerId, Long treatmentId, TreatmentUpdateRequestDto dto);

    void deleteTreatment(Long designerId, Long treatmentId);
}