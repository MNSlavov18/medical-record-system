package com.inf.medical_record_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientDTO {

    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    private String fullName;

    @NotBlank(message = "EGN is required")
    @Pattern(regexp = "\\d{10}", message = "EGN must contain exactly 10 digits")
    private String egn;

    @NotNull(message = "User is required")
    private Long userId;

    @NotNull(message = "Personal doctor is required")
    private Long personalDoctorId;
}