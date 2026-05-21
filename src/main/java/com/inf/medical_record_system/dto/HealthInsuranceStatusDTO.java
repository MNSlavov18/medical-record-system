package com.inf.medical_record_system.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HealthInsuranceStatusDTO {

    private Long id;

    private Long patientId;

    private String patientName;

    private int year;

    private int month;

    private boolean insured;
}