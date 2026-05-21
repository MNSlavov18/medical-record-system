package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.Diagnosis;
import com.inf.medical_record_system.data.entity.Doctor;
import com.inf.medical_record_system.data.entity.Examination;
import com.inf.medical_record_system.data.entity.Patient;
import com.inf.medical_record_system.data.entity.PaymentSource;
import com.inf.medical_record_system.data.repo.DiagnosisRepository;
import com.inf.medical_record_system.data.repo.DoctorRepository;
import com.inf.medical_record_system.data.repo.ExaminationRepository;
import com.inf.medical_record_system.data.repo.PatientRepository;
import com.inf.medical_record_system.data.repo.SickLeaveRepository;
import com.inf.medical_record_system.data.repo.TreatmentRepository;
import com.inf.medical_record_system.dto.ExaminationDTO;
import com.inf.medical_record_system.dto.ExaminationRequestDTO;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.ExaminationService;
import com.inf.medical_record_system.service.HealthInsuranceStatusService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExaminationServiceImpl implements ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final HealthInsuranceStatusService healthInsuranceStatusService;
    private final TreatmentRepository treatmentRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final CurrentUserService currentUserService;

    public ExaminationServiceImpl(
            ExaminationRepository examinationRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DiagnosisRepository diagnosisRepository,
            HealthInsuranceStatusService healthInsuranceStatusService,
            TreatmentRepository treatmentRepository,
            SickLeaveRepository sickLeaveRepository,
            CurrentUserService currentUserService
    ) {
        this.examinationRepository = examinationRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.healthInsuranceStatusService = healthInsuranceStatusService;
        this.treatmentRepository = treatmentRepository;
        this.sickLeaveRepository = sickLeaveRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public List<ExaminationDTO> getAllExaminations() {
        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return examinationRepository.findByPatientId(currentPatientId)
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        return examinationRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ExaminationDTO getExaminationById(Long id) {
        Examination examination = findExaminationById(id);
        validateCanReadExamination(examination);

        return mapToDTO(examination);
    }

    @Override
    public List<ExaminationDTO> getExaminationsByPatient(Long patientId) {
        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            if (!currentPatientId.equals(patientId)) {
                throw new InvalidOperationException("Patients can view only their own examinations");
            }
        }

        return examinationRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<ExaminationDTO> getExaminationsByDoctor(Long doctorId) {
        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return examinationRepository.findByDoctorId(doctorId)
                    .stream()
                    .filter(examination -> examination.getPatient().getId().equals(currentPatientId))
                    .map(this::mapToDTO)
                    .toList();
        }

        return examinationRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<ExaminationDTO> getExaminationsByDiagnosis(Long diagnosisId) {
        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return examinationRepository.findByDiagnosisId(diagnosisId)
                    .stream()
                    .filter(examination -> examination.getPatient().getId().equals(currentPatientId))
                    .map(this::mapToDTO)
                    .toList();
        }

        return examinationRepository.findByDiagnosisId(diagnosisId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<ExaminationDTO> getExaminationsByPeriod(LocalDate startDate, LocalDate endDate) {
        validateDatePeriod(startDate, endDate);

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return examinationRepository.findByExaminationDateBetween(startDate, endDate)
                    .stream()
                    .filter(examination -> examination.getPatient().getId().equals(currentPatientId))
                    .map(this::mapToDTO)
                    .toList();
        }

        return examinationRepository.findByExaminationDateBetween(startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<ExaminationDTO> getExaminationsByDoctorAndPeriod(Long doctorId, LocalDate startDate, LocalDate endDate) {
        validateDatePeriod(startDate, endDate);

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            return examinationRepository.findByDoctorIdAndExaminationDateBetween(doctorId, startDate, endDate)
                    .stream()
                    .filter(examination -> examination.getPatient().getId().equals(currentPatientId))
                    .map(this::mapToDTO)
                    .toList();
        }

        return examinationRepository.findByDoctorIdAndExaminationDateBetween(doctorId, startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ExaminationDTO createExamination(ExaminationRequestDTO requestDTO) {
        validateCanCreateExaminationForDoctor(requestDTO.getDoctorId());

        Doctor doctor = findDoctorById(requestDTO.getDoctorId());
        Patient patient = findPatientById(requestDTO.getPatientId());
        Diagnosis diagnosis = findDiagnosisById(requestDTO.getDiagnosisId());

        Examination examination = new Examination();
        examination.setExaminationDate(requestDTO.getExaminationDate());
        examination.setDoctor(doctor);
        examination.setPatient(patient);
        examination.setDiagnosis(diagnosis);
        examination.setPrice(requestDTO.getPrice());
        examination.setPaymentSource(calculatePaymentSource(patient.getId(), requestDTO.getExaminationDate()));

        Examination savedExamination = examinationRepository.save(examination);

        return mapToDTO(savedExamination);
    }

    @Override
    public ExaminationDTO updateExamination(Long id, ExaminationRequestDTO requestDTO) {
        Examination examination = findExaminationById(id);
        validateCanModifyExamination(examination);

        validateCanCreateExaminationForDoctor(requestDTO.getDoctorId());

        Doctor doctor = findDoctorById(requestDTO.getDoctorId());
        Patient patient = findPatientById(requestDTO.getPatientId());
        Diagnosis diagnosis = findDiagnosisById(requestDTO.getDiagnosisId());

        examination.setExaminationDate(requestDTO.getExaminationDate());
        examination.setDoctor(doctor);
        examination.setPatient(patient);
        examination.setDiagnosis(diagnosis);
        examination.setPrice(requestDTO.getPrice());
        examination.setPaymentSource(calculatePaymentSource(patient.getId(), requestDTO.getExaminationDate()));

        Examination updatedExamination = examinationRepository.save(examination);

        return mapToDTO(updatedExamination);
    }

    @Override
    public void deleteExamination(Long id) {
        Examination examination = findExaminationById(id);
        validateCanModifyExamination(examination);

        if (treatmentRepository.findByExaminationId(id).isPresent()) {
            throw new InvalidOperationException("Examination cannot be deleted because it has a treatment connected to it");
        }

        if (sickLeaveRepository.findByExaminationId(id).isPresent()) {
            throw new InvalidOperationException("Examination cannot be deleted because it has a sick leave connected to it");
        }

        examinationRepository.delete(examination);
    }

    private void validateCanReadExamination(Examination examination) {
        if (currentUserService.isAdmin() || currentUserService.isDoctor()) {
            return;
        }

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            if (!examination.getPatient().getId().equals(currentPatientId)) {
                throw new InvalidOperationException("Patients can view only their own examinations");
            }
        }
    }

    private void validateCanModifyExamination(Examination examination) {
        if (currentUserService.isAdmin()) {
            return;
        }

        if (currentUserService.isDoctor()) {
            Long currentDoctorId = currentUserService.getCurrentDoctorId();

            if (!examination.getDoctor().getId().equals(currentDoctorId)) {
                throw new InvalidOperationException("Doctors can edit only examinations they performed");
            }

            return;
        }

        throw new InvalidOperationException("You do not have permission to modify examinations");
    }

    private void validateCanCreateExaminationForDoctor(Long doctorId) {
        if (currentUserService.isAdmin()) {
            return;
        }

        if (currentUserService.isDoctor()) {
            Long currentDoctorId = currentUserService.getCurrentDoctorId();

            if (!currentDoctorId.equals(doctorId)) {
                throw new InvalidOperationException("Doctors can create examinations only for themselves");
            }

            return;
        }

        throw new InvalidOperationException("You do not have permission to create examinations");
    }

    private PaymentSource calculatePaymentSource(Long patientId, LocalDate examinationDate) {
        boolean insured = healthInsuranceStatusService.isPatientInsuredForLastSixMonths(patientId, examinationDate);

        if (insured) {
            return PaymentSource.NHIF;
        }

        return PaymentSource.PATIENT;
    }

    private Examination findExaminationById(Long id) {
        return examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with id: " + id));
    }

    private Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    private Diagnosis findDiagnosisById(Long id) {
        return diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with id: " + id));
    }

    private void validateDatePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidOperationException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidOperationException("Start date cannot be after end date");
        }
    }

    private ExaminationDTO mapToDTO(Examination examination) {
        ExaminationDTO dto = new ExaminationDTO();

        dto.setId(examination.getId());
        dto.setExaminationDate(examination.getExaminationDate());

        dto.setDoctorId(examination.getDoctor().getId());
        dto.setDoctorName(examination.getDoctor().getFullName());

        dto.setPatientId(examination.getPatient().getId());
        dto.setPatientName(examination.getPatient().getFullName());

        dto.setDiagnosisId(examination.getDiagnosis().getId());
        dto.setDiagnosisName(examination.getDiagnosis().getName());

        dto.setPrice(examination.getPrice());
        dto.setPaymentSource(examination.getPaymentSource());

        return dto;
    }
}