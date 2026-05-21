package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.HealthInsuranceStatus;
import com.inf.medical_record_system.data.entity.Patient;
import com.inf.medical_record_system.data.repo.HealthInsuranceStatusRepository;
import com.inf.medical_record_system.data.repo.PatientRepository;
import com.inf.medical_record_system.dto.HealthInsuranceStatusDTO;
import com.inf.medical_record_system.dto.HealthInsuranceStatusRequestDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.HealthInsuranceStatusService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class HealthInsuranceStatusServiceImpl implements HealthInsuranceStatusService {

    private final HealthInsuranceStatusRepository healthInsuranceStatusRepository;
    private final PatientRepository patientRepository;
    private final CurrentUserService currentUserService;

    public HealthInsuranceStatusServiceImpl(
            HealthInsuranceStatusRepository healthInsuranceStatusRepository,
            PatientRepository patientRepository,
            CurrentUserService currentUserService
    ) {
        this.healthInsuranceStatusRepository = healthInsuranceStatusRepository;
        this.patientRepository = patientRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public List<HealthInsuranceStatusDTO> getAllStatuses() {
        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return healthInsuranceStatusRepository.findByPatientId(currentPatientId)
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        return healthInsuranceStatusRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public HealthInsuranceStatusDTO getStatusById(Long id) {
        HealthInsuranceStatus status = findStatusById(id);
        validateCanReadStatus(status);

        return mapToDTO(status);
    }

    @Override
    public List<HealthInsuranceStatusDTO> getStatusesByPatient(Long patientId) {
        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            if (!currentPatientId.equals(patientId)) {
                throw new InvalidOperationException("Patients can view only their own health insurance statuses");
            }
        }

        return healthInsuranceStatusRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public HealthInsuranceStatusDTO createStatus(HealthInsuranceStatusRequestDTO requestDTO) {
        Patient patient = findPatientById(requestDTO.getPatientId());

        Optional<HealthInsuranceStatus> existingStatus =
                healthInsuranceStatusRepository.findByPatientIdAndYearAndMonth(
                        requestDTO.getPatientId(),
                        requestDTO.getYear(),
                        requestDTO.getMonth()
                );

        if (existingStatus.isPresent()) {
            throw new DuplicateResourceException("Health insurance status already exists for this patient, year and month");
        }

        HealthInsuranceStatus status = new HealthInsuranceStatus();
        status.setPatient(patient);
        status.setYear(requestDTO.getYear());
        status.setMonth(requestDTO.getMonth());
        status.setInsured(requestDTO.isInsured());

        HealthInsuranceStatus savedStatus = healthInsuranceStatusRepository.save(status);

        return mapToDTO(savedStatus);
    }

    @Override
    public HealthInsuranceStatusDTO updateStatus(Long id, HealthInsuranceStatusRequestDTO requestDTO) {
        HealthInsuranceStatus status = findStatusById(id);
        Patient patient = findPatientById(requestDTO.getPatientId());

        Optional<HealthInsuranceStatus> existingStatus =
                healthInsuranceStatusRepository.findByPatientIdAndYearAndMonth(
                        requestDTO.getPatientId(),
                        requestDTO.getYear(),
                        requestDTO.getMonth()
                );

        if (existingStatus.isPresent() && !existingStatus.get().getId().equals(id)) {
            throw new DuplicateResourceException("Health insurance status already exists for this patient, year and month");
        }

        status.setPatient(patient);
        status.setYear(requestDTO.getYear());
        status.setMonth(requestDTO.getMonth());
        status.setInsured(requestDTO.isInsured());

        HealthInsuranceStatus updatedStatus = healthInsuranceStatusRepository.save(status);

        return mapToDTO(updatedStatus);
    }

    @Override
    public void deleteStatus(Long id) {
        if (!currentUserService.isAdmin()) {
            throw new InvalidOperationException("Only administrators can delete health insurance statuses");
        }

        HealthInsuranceStatus status = findStatusById(id);
        healthInsuranceStatusRepository.delete(status);
    }

    @Override
    public boolean isPatientInsuredForLastSixMonths(Long patientId, LocalDate referenceDate) {
        validateCanCheckPatientInsurance(patientId);
        findPatientById(patientId);

        YearMonth currentMonth = YearMonth.from(referenceDate);

        for (int i = 0; i < 6; i++) {
            YearMonth checkedMonth = currentMonth.minusMonths(i);

            HealthInsuranceStatus status = healthInsuranceStatusRepository
                    .findByPatientIdAndYearAndMonth(
                            patientId,
                            checkedMonth.getYear(),
                            checkedMonth.getMonthValue()
                    )
                    .orElse(null);

            if (status == null || !status.isInsured()) {
                return false;
            }
        }

        return true;
    }

    private void validateCanReadStatus(HealthInsuranceStatus status) {
        if (currentUserService.isAdmin() || currentUserService.isDoctor()) {
            return;
        }

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            if (!status.getPatient().getId().equals(currentPatientId)) {
                throw new InvalidOperationException("Patients can view only their own health insurance statuses");
            }

            return;
        }

        throw new InvalidOperationException("You do not have permission to view this health insurance status");
    }

    private void validateCanCheckPatientInsurance(Long patientId) {
        if (currentUserService.isAdmin() || currentUserService.isDoctor()) {
            return;
        }

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            if (!currentPatientId.equals(patientId)) {
                throw new InvalidOperationException("Patients can check only their own insurance status");
            }

            return;
        }

        throw new InvalidOperationException("You do not have permission to check this insurance status");
    }

    private HealthInsuranceStatus findStatusById(Long id) {
        return healthInsuranceStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Health insurance status not found with id: " + id));
    }

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    private HealthInsuranceStatusDTO mapToDTO(HealthInsuranceStatus status) {
        HealthInsuranceStatusDTO dto = new HealthInsuranceStatusDTO();

        dto.setId(status.getId());
        dto.setPatientId(status.getPatient().getId());
        dto.setPatientName(status.getPatient().getFullName());
        dto.setYear(status.getYear());
        dto.setMonth(status.getMonth());
        dto.setInsured(status.isInsured());

        return dto;
    }
}