package com.inf.medical_record_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpecialtyDTO {

    private Long id;

    @NotBlank(message = "Specialty name is required")
    @Size(min = 2, max = 100, message = "Specialty name must be between 2 and 100 characters")
    private String name;
}