package com.inf.medical_record_system.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "patients",
        indexes = {
                @Index(name = "idx_patient_personal_doctor", columnList = "personal_doctor_id")
        }
)
public class Patient extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true, length = 10)
    private String egn;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "personal_doctor_id", nullable = false)
    private Doctor personalDoctor;

    @OneToMany(mappedBy = "patient")
    private Set<HealthInsuranceStatus> healthInsuranceStatuses = new HashSet<>();

    @OneToMany(mappedBy = "patient")
    private Set<Examination> examinations = new HashSet<>();
}