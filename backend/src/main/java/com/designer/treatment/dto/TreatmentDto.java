package com.designer.treatment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class TreatmentDto {

    private Long id;
    private Long designerId;
    private Long customerId;
    private LocalDate treatmentDate;
    private LocalTime treatmentTime;
    private String category;
    private String styleName;
    private String detail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
