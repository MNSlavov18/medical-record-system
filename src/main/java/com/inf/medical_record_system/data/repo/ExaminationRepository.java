package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByPatientId(Long patientId);

    List<Examination> findByDoctorId(Long doctorId);

    List<Examination> findByDiagnosisId(Long diagnosisId);

    List<Examination> findByDoctorIdAndExaminationDateBetween(
            Long doctorId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Examination> findByExaminationDateBetween(LocalDate startDate, LocalDate endDate);
}