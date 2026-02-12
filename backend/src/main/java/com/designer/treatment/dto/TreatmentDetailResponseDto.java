package com.designer.treatment.dto;

import com.designer.image.dto.ImageResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class TreatmentDetailResponseDto {

    private Long id;
    private Long customerId;

    private LocalDate treatmentDate;
    private LocalTime treatmentTime;

    private String category;
    private String styleName;
    private String detail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ImageResponseDto> images;
}
