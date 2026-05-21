package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.Examination;
import com.inf.medical_record_system.data.entity.Patient;
import com.inf.medical_record_system.data.entity.PaymentSource;
import com.inf.medical_record_system.data.repo.DoctorRepository;
import com.inf.medical_record_system.data.repo.ExaminationRepository;
import com.inf.medical_record_system.data.repo.PatientRepository;
import com.inf.medical_record_system.data.repo.SickLeaveRepository;
import com.inf.medical_record_system.dto.*;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final PatientRepository patientRepository;
    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final DoctorRepository doctorRepository;
    private final CurrentUserService currentUserService;

    public StatisticsServiceImpl(
            PatientRepository patientRepository,
            ExaminationRepository examinationRepository,
            SickLeaveRepository sickLeaveRepository,
            DoctorRepository doctorRepository,
            CurrentUserService currentUserService
    ) {
        this.patientRepository = patientRepository;
        this.examinationRepository = examinationRepository;
        this.sickLeaveRepository = sickLeaveRepository;
        this.doctorRepository = doctorRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public List<PatientDTO> getPatientsByDiagnosis(Long diagnosisId) {
        validateDoctorOrAdminAccess();

        return patientRepository.findPatientsByDiagnosisId(diagnosisId)
                .stream()
                .map(this::mapToPatientDTO)
                .toList();
    }

    @Override
    public DiagnosisCountDTO getMostCommonDiagnosis() {
        validateDoctorOrAdminAccess();

        List<Object[]> rows = examinationRepository.countExaminationsByDiagnosis();

        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("No examinations found for diagnosis statistics");
        }

        Object[] row = rows.get(0);

        return new DiagnosisCountDTO(
                toLong(row[0]),
                (String) row[1],
                toLong(row[2])
        );
    }

    @Override
    public List<PatientDTO> getPatientsByPersonalDoctor(Long doctorId) {
        validateDoctorOrAdminAccess();

        return patientRepository.findByPersonalDoctorId(doctorId)
                .stream()
                .map(this::mapToPatientDTO)
                .toList();
    }

    @Override
    public BigDecimal getTotalPatientPaidExaminationValue() {
        validateAdminAccess();

        BigDecimal total = examinationRepository.calculateTotalValueByPaymentSource(PaymentSource.PATIENT);

        return total == null ? BigDecimal.ZERO : total;
    }

    @Override
    public DoctorRevenueDTO getPatientPaidExaminationValueByDoctor(Long doctorId) {
        validateAdminAccess();

        var doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        BigDecimal total = examinationRepository.calculateTotalValueByPaymentSourceAndDoctor(
                PaymentSource.PATIENT,
                doctorId
        );

        return new DoctorRevenueDTO(
                doctor.getId(),
                doctor.getFullName(),
                total == null ? BigDecimal.ZERO : total
        );
    }

    @Override
    public List<DoctorPatientCountDTO> getPatientCountByPersonalDoctor() {
        validateAdminAccess();

        return patientRepository.countPatientsByPersonalDoctor()
                .stream()
                .map(row -> new DoctorPatientCountDTO(
                        toLong(row[0]),
                        (String) row[1],
                        toLong(row[2])
                ))
                .toList();
    }

    @Override
    public List<DoctorVisitCountDTO> getVisitCountByDoctor() {
        validateDoctorOrAdminAccess();

        return examinationRepository.countVisitsByDoctor()
                .stream()
                .map(row -> new DoctorVisitCountDTO(
                        toLong(row[0]),
                        (String) row[1],
                        toLong(row[2])
                ))
                .toList();
    }

    @Override
    public List<ExaminationDTO> getPatientVisitHistory(Long patientId) {
        validateDoctorOrAdminAccess();

        return examinationRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToExaminationDTO)
                .toList();
    }

    @Override
    public List<ExaminationDTO> searchExaminations(Long doctorId, LocalDate startDate, LocalDate endDate) {
        validateDoctorOrAdminAccess();

        boolean hasDoctor = doctorId != null;
        boolean hasStartDate = startDate != null;
        boolean hasEndDate = endDate != null;

        if (hasStartDate != hasEndDate) {
            throw new InvalidOperationException("Both start date and end date must be provided");
        }

        if (hasStartDate && startDate.isAfter(endDate)) {
            throw new InvalidOperationException("Start date cannot be after end date");
        }

        List<Examination> examinations;

        if (hasDoctor && hasStartDate) {
            examinations = examinationRepository.findByDoctorIdAndExaminationDateBetween(doctorId, startDate, endDate);
        } else if (hasDoctor) {
            examinations = examinationRepository.findByDoctorId(doctorId);
        } else if (hasStartDate) {
            examinations = examinationRepository.findByExaminationDateBetween(startDate, endDate);
        } else {
            examinations = examinationRepository.findAll();
        }

        return examinations
                .stream()
                .map(this::mapToExaminationDTO)
                .toList();
    }

    @Override
    public MonthSickLeaveCountDTO getMonthWithMostSickLeaves() {
        validateDoctorOrAdminAccess();

        List<Object[]> rows = sickLeaveRepository.countSickLeavesByMonth();

        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("No sick leaves found for monthly statistics");
        }

        Object[] row = rows.get(0);

        return new MonthSickLeaveCountDTO(
                toInteger(row[0]),
                toInteger(row[1]),
                toLong(row[2])
        );
    }

    @Override
    public List<DoctorSickLeaveCountDTO> getDoctorsWithMostSickLeaves() {
        validateDoctorOrAdminAccess();

        List<Object[]> rows = sickLeaveRepository.countSickLeavesByDoctor();

        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("No sick leaves found for doctor statistics");
        }

        Long maxCount = toLong(rows.get(0)[2]);

        return rows.stream()
                .filter(row -> toLong(row[2]).equals(maxCount))
                .map(row -> new DoctorSickLeaveCountDTO(
                        toLong(row[0]),
                        (String) row[1],
                        toLong(row[2])
                ))
                .toList();
    }

    private void validateAdminAccess() {
        if (!currentUserService.isAdmin()) {
            throw new InvalidOperationException("Only administrators can access this statistic");
        }
    }

    private void validateDoctorOrAdminAccess() {
        if (currentUserService.isAdmin() || currentUserService.isDoctor()) {
            return;
        }

        throw new InvalidOperationException("Only administrators and doctors can access this statistic");
    }

    private PatientDTO mapToPatientDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();

        dto.setId(patient.getId());
        dto.setFullName(patient.getFullName());
        dto.setEgn(patient.getEgn());
        dto.setUserId(patient.getUser().getId());
        dto.setUsername(patient.getUser().getUsername());
        dto.setPersonalDoctorId(patient.getPersonalDoctor().getId());
        dto.setPersonalDoctorName(patient.getPersonalDoctor().getFullName());

        return dto;
    }

    private ExaminationDTO mapToExaminationDTO(Examination examination) {
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

    private Long toLong(Object value) {
        return ((Number) value).longValue();
    }

    private Integer toInteger(Object value) {
        return ((Number) value).intValue();
    }
}