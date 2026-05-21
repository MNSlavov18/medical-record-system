package com.inf.medical_record_system.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SickLeaveDTO {

    private Long id;

    private LocalDate startDate;

    private int numberOfDays;

    private Long examinationId;

    private LocalDate examinationDate;

    private Long patientId;

    private String patientName;

    private Long doctorId;

    private String doctorName;
}