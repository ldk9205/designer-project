package com.designer.image.service;

import com.designer.image.dto.ImageDto;
import com.designer.image.dto.ImageResponseDto;
import com.designer.image.mapper.ImageMapper;
import com.designer.s3.service.S3ServiceImpl;
import com.designer.treatment.mapper.TreatmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final ImageMapper imageMapper;
    private final TreatmentMapper treatmentMapper;
    private final S3ServiceImpl s3ServiceImpl;

    /**
     * 시술 이미지 업로드
     */
    @Transactional
    public void uploadTreatmentImage(
            Long treatmentId,
            Long designerId,
            MultipartFile file
    ) {

        // 1️⃣ treatment 존재 및 소유권 확인
        Long ownerDesignerId =
                treatmentMapper.findDesignerIdByTreatmentId(treatmentId);

        if (ownerDesignerId == null) {
            throw new IllegalArgumentException("존재하지 않는 시술입니다.");
        }

        if (!ownerDesignerId.equals(designerId)) {
            throw new SecurityException("본인의 시술만 업로드할 수 있습니다.");
        }

        // 2️⃣ S3 key 생성
        String key = buildS3Key(
                designerId,
                treatmentId,
                file.getOriginalFilename()
        );

        try {
            // 3️⃣ S3 업로드
            s3ServiceImpl.uploadFile(key, file);

            // 4️⃣ DB 저장
            imageMapper.insertTreatmentImage(
                    treatmentId,
                    key,
                    file.getOriginalFilename()
            );

        } catch (Exception e) {

            // S3 업로드는 성공했는데 DB 실패했을 경우 대비
            try {
                s3ServiceImpl.deleteObject(key);
            } catch (Exception ignored) {
            }

            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.");
        }
    }


    /**
     * 특정 시술 이미지 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ImageResponseDto> getImagesByTreatmentId(
            Long treatmentId,
            Long designerId
    ) {

        // 1️⃣ 소유권 체크
        Long ownerDesignerId =
                treatmentMapper.findDesignerIdByTreatmentId(treatmentId);

        if (ownerDesignerId == null) {
            throw new IllegalArgumentException("존재하지 않는 시술입니다.");
        }

        if (!ownerDesignerId.equals(designerId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        // 2️⃣ DB 조회
        List<ImageDto> images =
                imageMapper.findByTreatmentId(treatmentId);

        // 3️⃣ presigned URL 변환
        return images.stream()
                .map(image -> ImageResponseDto.builder()
                        .id(image.getId())
                        .originalName(image.getOriginalName())
                        .imageUrl(
                                s3ServiceImpl.generateDownloadPresignedUrl(
                                        image.getImageUrl()
                                )
                        )
                        .build()
                )
                .toList();
    }

    /**
     * 이미지 삭제
     */
    @Transactional
    public void deleteImage(
            Long imageId,
            Long designerId
    ) {

        // 1️⃣ 이미지 조회
        ImageDto imageDto = imageMapper.findById(imageId);

        if (imageDto == null) {
            throw new IllegalArgumentException("존재하지 않는 이미지입니다.");
        }

        // 2️⃣ 소유권 확인
        Long ownerDesignerId =
                treatmentMapper.findDesignerIdByTreatmentId(imageDto.getTreatmentId());

        if (!ownerDesignerId.equals(designerId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        // 3️⃣ S3 삭제
        s3ServiceImpl.deleteObject(imageDto.getImageUrl());

        // 4️⃣ DB 삭제
        imageMapper.deleteById(imageId);
    }

    /**
     * S3 key 생성 규칙
     * ex) designerId/treatmentId/uuid_filename.jpg
     */
    private String buildS3Key(
            Long designerId,
            Long treatmentId,
            String originalFilename
    ) {

        String uuid = UUID.randomUUID().toString();

        return designerId +
                "/" +
                treatmentId +
                "/" +
                uuid +
                "_" +
                originalFilename;
    }
}

