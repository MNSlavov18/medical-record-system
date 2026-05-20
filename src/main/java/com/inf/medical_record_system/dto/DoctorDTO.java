package com.inf.medical_record_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorDTO {

    private Long id;

    @NotBlank(message = "Unique identification number is required")
    @Size(min = 3, max = 50, message = "Unique identification number must be between 3 and 50 characters")
    private String uniqueIdentificationNumber;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    private String fullName;

    @NotNull(message = "User is required")
    private Long userId;

    @NotNull(message = "Specialty is required")
    private Long specialtyId;

    private boolean canBePersonalDoctor;
}