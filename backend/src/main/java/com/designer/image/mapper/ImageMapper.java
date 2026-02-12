package com.designer.image.mapper;

import com.designer.image.dto.ImageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImageMapper {

    /**
     * 이미지 등록
     */
    void insertTreatmentImage(
            @Param("treatmentId") Long treatmentId,
            @Param("imageUrl") String imageUrl,
            @Param("originalName") String originalName
    );

    /**
     * 이미지 단건 조회
     */
    ImageDto findById(@Param("id") Long id);

    /**
     * 특정 treatment의 이미지 목록 조회
     */
    List<ImageDto> findByTreatmentId(@Param("treatmentId") Long treatmentId);

    /**
     * 이미지 삭제
     */
    void deleteById(@Param("id") Long id);
}
