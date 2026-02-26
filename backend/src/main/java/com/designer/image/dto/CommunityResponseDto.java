package com.designer.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityResponseDto {

    private Long imageId;
    private String customerName;
    private String treatmentDate;
    private String category;
    private String styleName;
    private String presignedUrl;
}