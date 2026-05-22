package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.HealthInsuranceStatusDTO;
import com.inf.medical_record_system.dto.HealthInsuranceStatusRequestDTO;
import com.inf.medical_record_system.dto.HealthInsurancePeriodRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface HealthInsuranceStatusService {

    List<HealthInsuranceStatusDTO> getAllStatuses();

    HealthInsuranceStatusDTO getStatusById(Long id);

    List<HealthInsuranceStatusDTO> getStatusesByPatient(Long patientId);

    HealthInsuranceStatusDTO createStatus(HealthInsuranceStatusRequestDTO requestDTO);

    List<HealthInsuranceStatusDTO> createStatusesForPeriod(HealthInsurancePeriodRequestDTO requestDTO);

    HealthInsuranceStatusDTO updateStatus(Long id, HealthInsuranceStatusRequestDTO requestDTO);

    void deleteStatus(Long id);

    boolean isPatientInsuredForLastSixMonths(Long patientId, LocalDate referenceDate);
}