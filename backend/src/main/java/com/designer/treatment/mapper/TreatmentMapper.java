package com.designer.treatment.mapper;

import com.designer.treatment.dto.TreatmentDto;
import com.designer.treatment.dto.TreatmentUpdateRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TreatmentMapper {

    // 시술 등록
    void insertTreatment(TreatmentDto dto);

    // 특정 고객의 시술 목록 조회
    List<TreatmentDto> selectTreatmentsByCustomerIdAndDesignerId(
            @Param("customerId") Long customerId,
            @Param("designerId") Long designerId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countTreatmentsByCustomerIdAndDesignerId(
            @Param("customerId") Long customerId,
            @Param("designerId") Long designerId
    );

    // 시술 단건 조회
    TreatmentDto selectTreatmentByIdAndDesignerId(
            @Param("treatmentId") Long treatmentId,
            @Param("designerId") Long designerId
    );

    // 시술 부분 수정
    int updateTreatmentByIdAndDesignerId(
            @Param("treatmentId") Long treatmentId,
            @Param("designerId") Long designerId,
            @Param("dto") TreatmentUpdateRequestDto dto
    );

    // 시술 삭제
    int deleteTreatmentByIdAndDesignerId(
            @Param("treatmentId") Long treatmentId,
            @Param("designerId") Long designerId
    );
}