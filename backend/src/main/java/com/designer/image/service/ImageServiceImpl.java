package com.designer.image.service;

import com.designer.image.dto.ImageDto;
import com.designer.image.dto.ImageResponseDto;
import com.designer.image.mapper.ImageMapper;
import com.designer.s3.service.S3ServiceImpl;
import com.designer.treatment.dto.TreatmentDto;
import com.designer.treatment.mapper.TreatmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.designer.image.dto.CommunityResponseDto;

import java.util.Map;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

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
        TreatmentDto treatment =
                treatmentMapper.selectTreatmentByIdAndDesignerId(treatmentId, designerId);

        if (treatment == null) {
            throw new IllegalArgumentException("존재하지 않는 시술이거나 본인 소유의 시술이 아닙니다.");
        }

        String key = buildS3Key(
                treatment.getCustomerId(),
                treatmentId,
                file.getOriginalFilename()
        );

        try {
            s3ServiceImpl.uploadFile(key, file);

            imageMapper.insertTreatmentImage(
                    treatmentId,
                    key,
                    file.getOriginalFilename()
            );

        } catch (Exception e) {
            try {
                s3ServiceImpl.deleteObject(key);
            } catch (Exception ignored) {
            }

            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
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
        if (treatmentMapper.selectTreatmentByIdAndDesignerId(treatmentId, designerId) == null) {
            throw new IllegalArgumentException("존재하지 않는 시술이거나 접근 권한이 없습니다.");
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
        if (treatmentMapper.selectTreatmentByIdAndDesignerId(imageDto.getTreatmentId(), designerId) == null) {
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
            Long customerId,
            Long treatmentId,
            String originalFilename
    ) {
        String uuid = UUID.randomUUID().toString();
        String extension = extractExtension(originalFilename);

        return customerId +
                "/" +
                treatmentId +
                "/" +
                uuid +
                extension;
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }

        int lastDotIndex = originalFilename.lastIndexOf(".");
        return originalFilename.substring(lastDotIndex).toLowerCase();
    }

    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getCommunityList() {

        // 1️⃣ DB에서 JOIN 조회
        List<Map<String, Object>> rows =
                imageMapper.findCommunityList();

        // 2️⃣ presigned URL 변환 + DTO 변환
        return rows.stream()
                .map(row -> {

                    Long imageId =
                            ((Number) row.get("imageId")).longValue();

                    String imageKey =
                            (String) row.get("imageUrl");

                    String treatmentDate =
                            row.get("treatmentDate").toString();

                    String category =
                            (String) row.get("category");

                    String styleName =
                            (String) row.get("styleName");

                    String customerName =
                            (String) row.get("customerName");

                    String presignedUrl =
                            s3ServiceImpl.generateDownloadPresignedUrl(
                                    imageKey
                            );

                    return new CommunityResponseDto(
                            imageId,
                            customerName,
                            treatmentDate,
                            category,
                            styleName,
                            presignedUrl
                    );
                })
                .toList();
    }
}