package com.designer.image.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ImageDto {

    private Long id;
    private Long treatmentId;
    private String imageUrl;
    private String originalName;
    private LocalDateTime createdAt;
}
