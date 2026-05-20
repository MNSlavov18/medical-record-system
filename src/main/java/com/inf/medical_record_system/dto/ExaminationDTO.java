package com.inf.medical_record_system.dto;

import com.inf.medical_record_system.data.entity.PaymentSource;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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

    @NotNull(message = "Examination date is required")
    @PastOrPresent(message = "Examination date cannot be in the future")
    private LocalDate examinationDate;

    @NotNull(message = "Doctor is required")
    private Long doctorId;

    @NotNull(message = "Patient is required")
    private Long patientId;

    @NotNull(message = "Diagnosis is required")
    private Long diagnosisId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal price;

    @NotNull(message = "Payment source is required")
    private PaymentSource paymentSource;
}