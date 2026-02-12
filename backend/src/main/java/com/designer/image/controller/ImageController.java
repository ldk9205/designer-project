package com.designer.image.controller;

import com.designer.image.dto.ImageResponseDto;
import com.designer.image.service.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {

    private final ImageServiceImpl imageServiceImpl;

    /**
     * 시술 이미지 업로드
     * POST /api/treatments/{id}/images
     */
    @PostMapping("/treatments/{id}/images")
    public ResponseEntity<?> uploadImage(
            @PathVariable("id") Long treatmentId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal(expression = "designerId") Long designerId
    ) {

        imageServiceImpl.uploadTreatmentImage(
                treatmentId,
                designerId,
                file
        );

        return ResponseEntity.ok().body("이미지 업로드 성공");
    }

    /**
     * 특정 시술의 이미지 목록 조회
     * GET /api/treatments/{id}/images
     */
    @GetMapping("/treatments/{id}/images")
    public ResponseEntity<List<ImageResponseDto>> getTreatmentImages(
            @PathVariable("id") Long treatmentId,
            @AuthenticationPrincipal(expression = "designerId") Long designerId
    ) {

        return ResponseEntity.ok(
                imageServiceImpl.getImagesByTreatmentId(
                        treatmentId,
                        designerId
                )
        );
    }

    /**
     * 이미지 삭제
     * DELETE /api/images/{id}
     */
    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> deleteImage(
            @PathVariable("id") Long imageId,
            @AuthenticationPrincipal(expression = "designerId") Long designerId
    ) {

        imageServiceImpl.deleteImage(imageId, designerId);

        return ResponseEntity.ok().body("이미지 삭제 성공");
    }
}
