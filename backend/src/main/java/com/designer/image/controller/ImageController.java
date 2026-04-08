package com.designer.image.controller;

import com.designer.image.dto.CommunityResponseDto;
import com.designer.image.dto.ImageResponseDto;
import com.designer.image.service.ImageServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {

    private final ImageServiceImpl imageServiceImpl;

    @PostMapping("/treatments/{id}/images")
    public ResponseEntity<?> uploadImage(
            @PathVariable("id") Long treatmentId,
            @RequestParam("file") MultipartFile file,
            @RequestAttribute("designerId") Long designerId,
            HttpServletRequest request
    ) {
        System.out.println("=== uploadImage 진입 ===");
        System.out.println("treatmentId=" + treatmentId);
        System.out.println("designerId=" + designerId);
        System.out.println("file=" + file.getOriginalFilename());
        System.out.println("fileSize=" + file.getSize());
        System.out.println("=== Claude Code 의견 ===");
        System.out.println("designerId attr = " + request.getAttribute("designerId"));
        imageServiceImpl.uploadTreatmentImage(
                treatmentId,
                designerId,
                file
        );

        return ResponseEntity.ok().body("이미지 업로드 성공");
    }

    @GetMapping("/treatments/{id}/images")
    public ResponseEntity<List<ImageResponseDto>> getTreatmentImages(
            @PathVariable("id") Long treatmentId,
            @RequestAttribute("designerId") Long designerId
    ) {
        return ResponseEntity.ok(
                imageServiceImpl.getImagesByTreatmentId(
                        treatmentId,
                        designerId
                )
        );
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> deleteImage(
            @PathVariable("id") Long imageId,
            @RequestAttribute("designerId") Long designerId
    ) {
        imageServiceImpl.deleteImage(imageId, designerId);
        return ResponseEntity.ok().body("이미지 삭제 성공");
    }

    @GetMapping("/community")
    public ResponseEntity<List<CommunityResponseDto>> getCommunityList() {
        return ResponseEntity.ok(
                imageServiceImpl.getCommunityList()
        );
    }
}