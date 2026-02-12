package com.designer.treatment.mapper;

import com.designer.treatment.dto.TreatmentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TreatmentMapper {

    /**
     * treatment의 designer_id 조회 (소유권 체크용)
     */
    Long findDesignerIdByTreatmentId(@Param("treatmentId") Long treatmentId);

    /**
     * treatment 단건 조회
     */
    TreatmentDto findById(@Param("id") Long id);

    /**
     * 특정 고객의 treatment 목록 조회
     */
    List<TreatmentDto> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * treatment 등록
     */
    void insertTreatment(TreatmentDto treatmentDto);

    /**
     * treatment 삭제
     */
    void deleteById(@Param("id") Long id);
}
