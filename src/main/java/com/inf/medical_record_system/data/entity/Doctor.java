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
        name = "doctors",
        indexes = {
                @Index(name = "idx_doctor_specialty", columnList = "specialty_id")
        }
)
public class Doctor extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String uniqueIdentificationNumber;

    @Column(nullable = false)
    private String fullName;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Column(nullable = false)
    private boolean canBePersonalDoctor;

    @OneToMany(mappedBy = "personalDoctor")
    private Set<Patient> personalPatients = new HashSet<>();

    @OneToMany(mappedBy = "doctor")
    private Set<Examination> examinations = new HashSet<>();
}