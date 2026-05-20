package com.inf.medical_record_system.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "health_insurance_statuses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_patient_insurance_month",
                        columnNames = {"patient_id", "insurance_year", "insurance_month"}
                )
        },
        indexes = {
                @Index(name = "idx_insurance_patient", columnList = "patient_id")
        }
)
public class HealthInsuranceStatus extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "insurance_year", nullable = false)
    private int year;

    @Column(name = "insurance_month", nullable = false)
    private int month;

    @Column(nullable = false)
    private boolean insured;
}