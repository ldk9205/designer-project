package com.designer.treatment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class TreatmentResponseDto {

    private Long id;
    private LocalDate treatmentDate;
    private LocalTime treatmentTime;
    private String category;
    private String styleName;
}
