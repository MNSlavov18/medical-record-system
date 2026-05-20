package com.inf.medical_record_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiagnosisDTO {

    private Long id;

    @NotBlank(message = "Diagnosis code is required")
    @Size(max = 50, message = "Diagnosis code must be maximum 50 characters")
    private String code;

    @NotBlank(message = "Diagnosis name is required")
    @Size(min = 2, max = 150, message = "Diagnosis name must be between 2 and 150 characters")
    private String name;

    @Size(max = 1000, message = "Description must be maximum 1000 characters")
    private String description;
}