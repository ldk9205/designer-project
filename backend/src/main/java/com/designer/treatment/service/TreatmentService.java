package com.designer.treatment.service;

import com.designer.treatment.dto.TreatmentCreateRequestDto;
import com.designer.treatment.dto.TreatmentDetailResponseDto;
import com.designer.treatment.dto.TreatmentResponseDto;

import java.util.List;

public interface TreatmentService {

    /**
     * 시술 등록
     */
    void createTreatment(
            TreatmentCreateRequestDto request,
            Long designerId
    );

    /**
     * 고객별 시술 목록 조회
     */
    List<TreatmentResponseDto> getTreatmentsByCustomer(
            Long customerId,
            Long designerId
    );

    /**
     * 시술 상세 조회 (이미지 포함)
     */
    TreatmentDetailResponseDto getTreatmentDetail(
            Long treatmentId,
            Long designerId
    );

    /**
     * 시술 삭제
     */
    void deleteTreatment(
            Long treatmentId,
            Long designerId
    );
}
