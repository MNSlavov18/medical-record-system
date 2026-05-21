package com.inf.medical_record_system.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientDTO {

    private Long id;

    private String fullName;

    private String egn;

    private Long userId;

    private String username;

    private Long personalDoctorId;

    private String personalDoctorName;
}