package com.inf.medical_record_system.dto;

import com.inf.medical_record_system.data.entity.PaymentSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ExaminationDTO {

    private Long id;

    private LocalDate examinationDate;

    private Long doctorId;

    private String doctorName;

    private Long patientId;

    private String patientName;

    private Long diagnosisId;

    private String diagnosisName;

    private BigDecimal price;

    private PaymentSource paymentSource;
}