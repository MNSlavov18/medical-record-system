package com.inf.medical_record_system.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "examinations",
        indexes = {
                @Index(name = "idx_examination_doctor", columnList = "doctor_id"),
                @Index(name = "idx_examination_patient", columnList = "patient_id"),
                @Index(name = "idx_examination_diagnosis", columnList = "diagnosis_id"),
                @Index(name = "idx_examination_date", columnList = "examination_date")
        }
)
public class Examination extends BaseEntity {

    @Column(nullable = false)
    private LocalDate examinationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private Diagnosis diagnosis;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentSource paymentSource;

    @OneToOne(mappedBy = "examination")
    private Treatment treatment;

    @OneToOne(mappedBy = "examination")
    private SickLeave sickLeave;
}