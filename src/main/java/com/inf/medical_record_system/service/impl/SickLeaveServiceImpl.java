package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.Examination;
import com.inf.medical_record_system.data.entity.SickLeave;
import com.inf.medical_record_system.data.repo.ExaminationRepository;
import com.inf.medical_record_system.data.repo.SickLeaveRepository;
import com.inf.medical_record_system.dto.SickLeaveDTO;
import com.inf.medical_record_system.dto.SickLeaveRequestDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.SickLeaveService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SickLeaveServiceImpl implements SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;
    private final ExaminationRepository examinationRepository;
    private final CurrentUserService currentUserService;

    public SickLeaveServiceImpl(
            SickLeaveRepository sickLeaveRepository,
            ExaminationRepository examinationRepository,
            CurrentUserService currentUserService
    ) {
        this.sickLeaveRepository = sickLeaveRepository;
        this.examinationRepository = examinationRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public List<SickLeaveDTO> getAllSickLeaves() {
        if (currentUserService.isAdmin()) {
            return sickLeaveRepository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        if (currentUserService.isDoctor()) {
            Long currentDoctorId = currentUserService.getCurrentDoctorId();

            return sickLeaveRepository.findAll()
                    .stream()
                    .filter(sickLeave -> sickLeave.getExamination().getDoctor().getId().equals(currentDoctorId))
                    .map(this::mapToDTO)
                    .toList();
        }

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return sickLeaveRepository.findAll()
                    .stream()
                    .filter(sickLeave -> sickLeave.getExamination().getPatient().getId().equals(currentPatientId))
                    .map(this::mapToDTO)
                    .toList();
        }

        throw new InvalidOperationException("You do not have permission to view sick leaves");
    }

    @Override
    public SickLeaveDTO getSickLeaveById(Long id) {
        SickLeave sickLeave = findSickLeaveById(id);
        validateCanReadSickLeave(sickLeave);

        return mapToDTO(sickLeave);
    }

    @Override
    public SickLeaveDTO getSickLeaveByExamination(Long examinationId) {
        SickLeave sickLeave = sickLeaveRepository.findByExaminationId(examinationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sick leave not found for examination with id: " + examinationId
                ));

        validateCanReadSickLeave(sickLeave);

        return mapToDTO(sickLeave);
    }

    @Override
    public List<SickLeaveDTO> getSickLeavesByPatient(Long patientId) {
        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            if (!currentPatientId.equals(patientId)) {
                throw new InvalidOperationException("Patients can view only their own sick leaves");
            }
        }

        return sickLeaveRepository.findByExaminationPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<SickLeaveDTO> getSickLeavesByDoctor(Long doctorId) {
        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return sickLeaveRepository.findByExaminationDoctorId(doctorId)
                    .stream()
                    .filter(sickLeave -> sickLeave.getExamination().getPatient().getId().equals(currentPatientId))
                    .map(this::mapToDTO)
                    .toList();
        }

        return sickLeaveRepository.findByExaminationDoctorId(doctorId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<SickLeaveDTO> getSickLeavesByPeriod(LocalDate startDate, LocalDate endDate) {
        validateDatePeriod(startDate, endDate);

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return sickLeaveRepository.findByStartDateBetween(startDate, endDate)
                    .stream()
                    .filter(sickLeave -> sickLeave.getExamination().getPatient().getId().equals(currentPatientId))
                    .map(this::mapToDTO)
                    .toList();
        }

        return sickLeaveRepository.findByStartDateBetween(startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public SickLeaveDTO createSickLeave(SickLeaveRequestDTO requestDTO) {
        Examination examination = findExaminationById(requestDTO.getExaminationId());

        validateCanModifySickLeaveForExamination(examination);
        validateSickLeaveStartDate(requestDTO.getStartDate(), examination.getExaminationDate());

        if (sickLeaveRepository.findByExaminationId(examination.getId()).isPresent()) {
            throw new DuplicateResourceException("This examination already has a sick leave");
        }

        SickLeave sickLeave = new SickLeave();
        sickLeave.setStartDate(requestDTO.getStartDate());
        sickLeave.setNumberOfDays(requestDTO.getNumberOfDays());
        sickLeave.setExamination(examination);

        SickLeave savedSickLeave = sickLeaveRepository.save(sickLeave);

        return mapToDTO(savedSickLeave);
    }

    @Override
    public SickLeaveDTO updateSickLeave(Long id, SickLeaveRequestDTO requestDTO) {
        SickLeave sickLeave = findSickLeaveById(id);
        validateCanModifySickLeave(sickLeave);

        Examination examination = findExaminationById(requestDTO.getExaminationId());

        validateCanModifySickLeaveForExamination(examination);
        validateSickLeaveStartDate(requestDTO.getStartDate(), examination.getExaminationDate());

        sickLeaveRepository.findByExaminationId(examination.getId())
                .ifPresent(existingSickLeave -> {
                    if (!existingSickLeave.getId().equals(id)) {
                        throw new DuplicateResourceException("This examination already has a sick leave");
                    }
                });

        sickLeave.setStartDate(requestDTO.getStartDate());
        sickLeave.setNumberOfDays(requestDTO.getNumberOfDays());
        sickLeave.setExamination(examination);

        SickLeave updatedSickLeave = sickLeaveRepository.save(sickLeave);

        return mapToDTO(updatedSickLeave);
    }

    @Override
    public void deleteSickLeave(Long id) {
        SickLeave sickLeave = findSickLeaveById(id);
        validateCanModifySickLeave(sickLeave);

        sickLeaveRepository.delete(sickLeave);
    }

    private void validateCanReadSickLeave(SickLeave sickLeave) {
        if (currentUserService.isAdmin() || currentUserService.isDoctor()) {
            return;
        }

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            if (!sickLeave.getExamination().getPatient().getId().equals(currentPatientId)) {
                throw new InvalidOperationException("Patients can view only their own sick leaves");
            }

            return;
        }

        throw new InvalidOperationException("You do not have permission to view this sick leave");
    }

    private void validateCanModifySickLeave(SickLeave sickLeave) {
        validateCanModifySickLeaveForExamination(sickLeave.getExamination());
    }

    private void validateCanModifySickLeaveForExamination(Examination examination) {
        if (currentUserService.isAdmin()) {
            return;
        }

        if (currentUserService.isDoctor()) {
            Long currentDoctorId = currentUserService.getCurrentDoctorId();

            if (!examination.getDoctor().getId().equals(currentDoctorId)) {
                throw new InvalidOperationException("Doctors can modify only sick leaves connected to their own examinations");
            }

            return;
        }

        throw new InvalidOperationException("You do not have permission to modify sick leaves");
    }

    private SickLeave findSickLeaveById(Long id) {
        return sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sick leave not found with id: " + id));
    }

    private Examination findExaminationById(Long id) {
        return examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with id: " + id));
    }

    private void validateDatePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidOperationException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidOperationException("Start date cannot be after end date");
        }
    }

    private void validateSickLeaveStartDate(LocalDate sickLeaveStartDate, LocalDate examinationDate) {
        if (sickLeaveStartDate.isBefore(examinationDate)) {
            throw new InvalidOperationException("Sick leave start date cannot be before the examination date");
        }
    }

    private SickLeaveDTO mapToDTO(SickLeave sickLeave) {
        SickLeaveDTO dto = new SickLeaveDTO();

        dto.setId(sickLeave.getId());
        dto.setStartDate(sickLeave.getStartDate());
        dto.setNumberOfDays(sickLeave.getNumberOfDays());

        Examination examination = sickLeave.getExamination();

        dto.setExaminationId(examination.getId());
        dto.setExaminationDate(examination.getExaminationDate());

        dto.setPatientId(examination.getPatient().getId());
        dto.setPatientName(examination.getPatient().getFullName());

        dto.setDoctorId(examination.getDoctor().getId());
        dto.setDoctorName(examination.getDoctor().getFullName());

        return dto;
    }
}