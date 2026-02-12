package com.designer.image.service;

import com.designer.image.dto.ImageResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    /**
     * 시술 이미지 업로드
     */
    void uploadTreatmentImage(
            Long treatmentId,
            Long designerId,
            MultipartFile file
    );

    /**
     * 특정 시술 이미지 목록 조회
     */
    List<ImageResponseDto> getImagesByTreatmentId(
            Long treatmentId,
            Long designerId
    );

    /**
     * 이미지 삭제
     */
    void deleteImage(
            Long imageId,
            Long designerId
    );
}
