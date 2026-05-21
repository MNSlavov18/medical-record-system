package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.Examination;
import com.inf.medical_record_system.data.entity.Treatment;
import com.inf.medical_record_system.data.repo.ExaminationRepository;
import com.inf.medical_record_system.data.repo.TreatmentRepository;
import com.inf.medical_record_system.dto.TreatmentDTO;
import com.inf.medical_record_system.dto.TreatmentRequestDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.TreatmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final ExaminationRepository examinationRepository;
    private final CurrentUserService currentUserService;

    public TreatmentServiceImpl(
            TreatmentRepository treatmentRepository,
            ExaminationRepository examinationRepository,
            CurrentUserService currentUserService
    ) {
        this.treatmentRepository = treatmentRepository;
        this.examinationRepository = examinationRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public List<TreatmentDTO> getAllTreatments() {
        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return treatmentRepository.findAll()
                    .stream()
                    .filter(treatment -> treatment.getExamination().getPatient().getId().equals(currentPatientId))
                    .map(this::mapToDTO)
                    .toList();
        }

        return treatmentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public TreatmentDTO getTreatmentById(Long id) {
        Treatment treatment = findTreatmentById(id);
        validateCanReadTreatment(treatment);

        return mapToDTO(treatment);
    }

    @Override
    public TreatmentDTO getTreatmentByExamination(Long examinationId) {
        Treatment treatment = treatmentRepository.findByExaminationId(examinationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Treatment not found for examination with id: " + examinationId
                ));

        validateCanReadTreatment(treatment);

        return mapToDTO(treatment);
    }

    @Override
    public TreatmentDTO createTreatment(TreatmentRequestDTO requestDTO) {
        validateTreatmentDates(requestDTO.getStartDate(), requestDTO.getEndDate());

        Examination examination = findExaminationById(requestDTO.getExaminationId());
        validateCanModifyTreatmentForExamination(examination);

        if (treatmentRepository.findByExaminationId(examination.getId()).isPresent()) {
            throw new DuplicateResourceException("This examination already has a treatment");
        }

        Treatment treatment = new Treatment();
        treatment.setDescription(requestDTO.getDescription());
        treatment.setStartDate(requestDTO.getStartDate());
        treatment.setEndDate(requestDTO.getEndDate());
        treatment.setPrescribedMedication(requestDTO.getPrescribedMedication());
        treatment.setExamination(examination);

        Treatment savedTreatment = treatmentRepository.save(treatment);

        return mapToDTO(savedTreatment);
    }

    @Override
    public TreatmentDTO updateTreatment(Long id, TreatmentRequestDTO requestDTO) {
        validateTreatmentDates(requestDTO.getStartDate(), requestDTO.getEndDate());

        Treatment treatment = findTreatmentById(id);
        validateCanModifyTreatment(treatment);

        Examination examination = findExaminationById(requestDTO.getExaminationId());
        validateCanModifyTreatmentForExamination(examination);

        treatmentRepository.findByExaminationId(examination.getId())
                .ifPresent(existingTreatment -> {
                    if (!existingTreatment.getId().equals(id)) {
                        throw new DuplicateResourceException("This examination already has a treatment");
                    }
                });

        treatment.setDescription(requestDTO.getDescription());
        treatment.setStartDate(requestDTO.getStartDate());
        treatment.setEndDate(requestDTO.getEndDate());
        treatment.setPrescribedMedication(requestDTO.getPrescribedMedication());
        treatment.setExamination(examination);

        Treatment updatedTreatment = treatmentRepository.save(treatment);

        return mapToDTO(updatedTreatment);
    }

    @Override
    public void deleteTreatment(Long id) {
        Treatment treatment = findTreatmentById(id);
        validateCanModifyTreatment(treatment);

        treatmentRepository.delete(treatment);
    }

    private void validateCanReadTreatment(Treatment treatment) {
        if (currentUserService.isAdmin() || currentUserService.isDoctor()) {
            return;
        }

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            if (!treatment.getExamination().getPatient().getId().equals(currentPatientId)) {
                throw new InvalidOperationException("Patients can view only their own treatments");
            }

            return;
        }

        throw new InvalidOperationException("You do not have permission to view this treatment");
    }

    private void validateCanModifyTreatment(Treatment treatment) {
        validateCanModifyTreatmentForExamination(treatment.getExamination());
    }

    private void validateCanModifyTreatmentForExamination(Examination examination) {
        if (currentUserService.isAdmin()) {
            return;
        }

        if (currentUserService.isDoctor()) {
            Long currentDoctorId = currentUserService.getCurrentDoctorId();

            if (!examination.getDoctor().getId().equals(currentDoctorId)) {
                throw new InvalidOperationException("Doctors can modify only treatments connected to their own examinations");
            }

            return;
        }

        throw new InvalidOperationException("You do not have permission to modify treatments");
    }

    private Treatment findTreatmentById(Long id) {
        return treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + id));
    }

    private Examination findExaminationById(Long id) {
        return examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with id: " + id));
    }

    private void validateTreatmentDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidOperationException("Treatment start date cannot be after end date");
        }
    }

    private TreatmentDTO mapToDTO(Treatment treatment) {
        TreatmentDTO dto = new TreatmentDTO();

        dto.setId(treatment.getId());
        dto.setDescription(treatment.getDescription());
        dto.setStartDate(treatment.getStartDate());
        dto.setEndDate(treatment.getEndDate());
        dto.setPrescribedMedication(treatment.getPrescribedMedication());

        Examination examination = treatment.getExamination();

        dto.setExaminationId(examination.getId());
        dto.setExaminationDate(examination.getExaminationDate());

        dto.setPatientId(examination.getPatient().getId());
        dto.setPatientName(examination.getPatient().getFullName());

        dto.setDoctorId(examination.getDoctor().getId());
        dto.setDoctorName(examination.getDoctor().getFullName());

        return dto;
    }
}