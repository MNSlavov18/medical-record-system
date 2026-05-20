package com.inf.medical_record_system.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorDTO {

    private Long id;

    private String uniqueIdentificationNumber;

    private String fullName;

    private Long userId;

    private String username;

    private Long specialtyId;

    private String specialtyName;

    private boolean canBePersonalDoctor;
}