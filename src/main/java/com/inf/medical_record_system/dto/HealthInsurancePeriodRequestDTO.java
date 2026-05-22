package com.inf.medical_record_system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HealthInsurancePeriodRequestDTO {

    @NotNull(message = "Patient is required")
    private Long patientId;

    @NotNull(message = "Start year is required")
    @Min(value = 2000, message = "Start year must be after 2000")
    @Max(value = 2100, message = "Start year must be before 2100")
    private Integer startYear;

    @NotNull(message = "Start month is required")
    @Min(value = 1, message = "Start month must be between 1 and 12")
    @Max(value = 12, message = "Start month must be between 1 and 12")
    private Integer startMonth;

    @NotNull(message = "End year is required")
    @Min(value = 2000, message = "End year must be after 2000")
    @Max(value = 2100, message = "End year must be before 2100")
    private Integer endYear;

    @NotNull(message = "End month is required")
    @Min(value = 1, message = "End month must be between 1 and 12")
    @Max(value = 12, message = "End month must be between 1 and 12")
    private Integer endMonth;

    private boolean insured;
}