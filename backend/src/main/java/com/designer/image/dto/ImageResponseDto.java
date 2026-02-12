package com.designer.image.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponseDto {

    private Long id;
    private String originalName;
    private String imageUrl; // presigned download url
}
