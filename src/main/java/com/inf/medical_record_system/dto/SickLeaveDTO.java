package com.inf.medical_record_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SickLeaveDTO {

    private Long id;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Min(value = 1, message = "Number of days must be at least 1")
    private int numberOfDays;

    @NotNull(message = "Examination is required")
    private Long examinationId;
}