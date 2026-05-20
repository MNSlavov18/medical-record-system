package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEgn(String egn);

    Optional<Patient> findByUserId(Long userId);

    List<Patient> findByPersonalDoctorId(Long personalDoctorId);

    boolean existsByEgn(String egn);
}