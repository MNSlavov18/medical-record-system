package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.HealthInsuranceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthInsuranceStatusRepository extends JpaRepository<HealthInsuranceStatus, Long> {

    List<HealthInsuranceStatus> findByPatientId(Long patientId);

    Optional<HealthInsuranceStatus> findByPatientIdAndYearAndMonth(Long patientId, int year, int month);
}