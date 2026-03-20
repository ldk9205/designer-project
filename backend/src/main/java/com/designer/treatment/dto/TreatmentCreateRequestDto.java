package com.designer.treatment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class TreatmentCreateRequestDto {

    @NotNull(message = "고객 ID는 필수입니다.")
    private Long customerId;

    @NotNull(message = "시술 날짜는 필수입니다.")
    private LocalDate treatmentDate;

    private LocalTime treatmentTime;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    private String styleName;
    private String detail;
}
