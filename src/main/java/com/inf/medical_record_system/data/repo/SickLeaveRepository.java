package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    Optional<SickLeave> findByExaminationId(Long examinationId);

    List<SickLeave> findByExaminationPatientId(Long patientId);

    List<SickLeave> findByExaminationDoctorId(Long doctorId);

    List<SickLeave> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
}