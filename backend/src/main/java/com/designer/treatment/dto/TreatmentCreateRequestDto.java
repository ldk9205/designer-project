package com.designer.treatment.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class TreatmentCreateRequestDto {

    private Long customerId;
    private LocalDate treatmentDate;
    private LocalTime treatmentTime;
    private String category;
    private String styleName;
    private String detail;
}
