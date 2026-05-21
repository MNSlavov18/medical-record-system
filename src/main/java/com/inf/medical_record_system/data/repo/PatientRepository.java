package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEgn(String egn);

    Optional<Patient> findByUserId(Long userId);

    List<Patient> findByPersonalDoctorId(Long personalDoctorId);

    boolean existsByEgn(String egn);

    @Query("""
            SELECT DISTINCT e.patient
            FROM Examination e
            WHERE e.diagnosis.id = :diagnosisId
            """)
    List<Patient> findPatientsByDiagnosisId(@Param("diagnosisId") Long diagnosisId);

    @Query("""
            SELECT p.personalDoctor.id, p.personalDoctor.fullName, COUNT(p)
            FROM Patient p
            GROUP BY p.personalDoctor.id, p.personalDoctor.fullName
            ORDER BY COUNT(p) DESC
            """)
    List<Object[]> countPatientsByPersonalDoctor();
}