package com.inf.medical_record_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TreatmentRequestDTO {

    @NotBlank(message = "Treatment description is required")
    @Size(max = 2000, message = "Treatment description must be maximum 2000 characters")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 2000, message = "Prescribed medication must be maximum 2000 characters")
    private String prescribedMedication;

    @NotNull(message = "Examination is required")
    private Long examinationId;
}