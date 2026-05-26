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
import com.inf.medical_record_system.service.HealthInsuranceStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = ExaminationServiceImpl.class)
class ExaminationServiceImplTest {

    @MockitoBean
    private ExaminationRepository examinationRepository;

    @MockitoBean
    private DoctorRepository doctorRepository;

    @MockitoBean
    private PatientRepository patientRepository;

    @MockitoBean
    private DiagnosisRepository diagnosisRepository;

    @MockitoBean
    private HealthInsuranceStatusService healthInsuranceStatusService;

    @MockitoBean
    private TreatmentRepository treatmentRepository;

    @MockitoBean
    private SickLeaveRepository sickLeaveRepository;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Autowired
    private ExaminationServiceImpl examinationService;

    private Doctor doctor;
    private Doctor anotherDoctor;
    private Patient patient;
    private Patient anotherPatient;
    private Diagnosis diagnosis;

    @BeforeEach
    void init() {
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFullName("Dr. Ivan Petrov");

        anotherDoctor = new Doctor();
        anotherDoctor.setId(2L);
        anotherDoctor.setFullName("Dr. Georgi Nikolov");

        patient = new Patient();
        patient.setId(1L);
        patient.setFullName("Mario Slavov");
        patient.setEgn("9901011234");

        anotherPatient = new Patient();
        anotherPatient.setId(2L);
        anotherPatient.setFullName("Alex Petrov");
        anotherPatient.setEgn("9802021234");

        diagnosis = new Diagnosis();
        diagnosis.setId(1L);
        diagnosis.setCode("J00");
        diagnosis.setName("Common cold");
        diagnosis.setDescription("Acute nasopharyngitis");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createExaminationShouldSetPaymentSourceToNhifWhenPatientIsInsured() {
        ExaminationRequestDTO requestDTO = createRequestDTO();

        given(currentUserService.isAdmin()).willReturn(true);

        given(doctorRepository.findById(1L)).willReturn(Optional.of(doctor));
        given(patientRepository.findById(1L)).willReturn(Optional.of(patient));
        given(diagnosisRepository.findById(1L)).willReturn(Optional.of(diagnosis));

        given(healthInsuranceStatusService.isPatientInsuredForLastSixMonths(
                1L,
                requestDTO.getExaminationDate()
        )).willReturn(true);

        given(examinationRepository.save(any(Examination.class)))
                .willAnswer(invocation -> {
                    Examination examination = invocation.getArgument(0);
                    examination.setId(1L);
                    return examination;
                });

        ExaminationDTO result = examinationService.createExamination(requestDTO);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPatientId()).isEqualTo(1L);
        assertThat(result.getDoctorId()).isEqualTo(1L);
        assertThat(result.getDiagnosisId()).isEqualTo(1L);
        assertThat(result.getPaymentSource()).isEqualTo(PaymentSource.NHIF);

        verify(examinationRepository, times(1)).save(any(Examination.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createExaminationShouldSetPaymentSourceToPatientWhenPatientIsNotInsured() {
        ExaminationRequestDTO requestDTO = createRequestDTO();

        given(currentUserService.isAdmin()).willReturn(true);

        given(doctorRepository.findById(1L)).willReturn(Optional.of(doctor));
        given(patientRepository.findById(1L)).willReturn(Optional.of(patient));
        given(diagnosisRepository.findById(1L)).willReturn(Optional.of(diagnosis));

        given(healthInsuranceStatusService.isPatientInsuredForLastSixMonths(
                1L,
                requestDTO.getExaminationDate()
        )).willReturn(false);

        given(examinationRepository.save(any(Examination.class)))
                .willAnswer(invocation -> {
                    Examination examination = invocation.getArgument(0);
                    examination.setId(2L);
                    return examination;
                });

        ExaminationDTO result = examinationService.createExamination(requestDTO);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getPaymentSource()).isEqualTo(PaymentSource.PATIENT);

        verify(examinationRepository, times(1)).save(any(Examination.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getExaminationByIdShouldReturnExaminationWhenExists() {
        Examination examination = createExamination(1L, doctor, patient);

        given(examinationRepository.findById(1L)).willReturn(Optional.of(examination));
        given(currentUserService.isAdmin()).willReturn(true);

        ExaminationDTO result = examinationService.getExaminationById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDoctorName()).isEqualTo("Dr. Ivan Petrov");
        assertThat(result.getPatientName()).isEqualTo("Mario Slavov");
        assertThat(result.getDiagnosisName()).isEqualTo("Common cold");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getExaminationByIdShouldThrowExceptionWhenExaminationDoesNotExist() {
        given(examinationRepository.findById(99L)).willReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> examinationService.getExaminationById(99L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Examination not found with id: 99");
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getExaminationByIdShouldThrowExceptionWhenPatientTriesToViewAnotherPatientsExamination() {
        Examination examination = createExamination(1L, doctor, anotherPatient);

        given(examinationRepository.findById(1L)).willReturn(Optional.of(examination));

        given(currentUserService.isAdmin()).willReturn(false);
        given(currentUserService.isDoctor()).willReturn(false);
        given(currentUserService.isPatient()).willReturn(true);
        given(currentUserService.getCurrentPatientId()).willReturn(1L);

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> examinationService.getExaminationById(1L)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Patients can view only their own examinations");
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void updateExaminationShouldThrowExceptionWhenDoctorTriesToUpdateAnotherDoctorsExamination() {
        Examination existingExamination = createExamination(1L, anotherDoctor, patient);
        ExaminationRequestDTO requestDTO = createRequestDTO();

        given(examinationRepository.findById(1L)).willReturn(Optional.of(existingExamination));

        given(currentUserService.isAdmin()).willReturn(false);
        given(currentUserService.isDoctor()).willReturn(true);
        given(currentUserService.getCurrentDoctorId()).willReturn(1L);

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> examinationService.updateExamination(1L, requestDTO)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Doctors can edit only examinations they performed");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllExaminationsShouldReturnAllExaminationsForAdmin() {
        Examination firstExamination = createExamination(1L, doctor, patient);
        Examination secondExamination = createExamination(2L, anotherDoctor, anotherPatient);

        given(currentUserService.isAdmin()).willReturn(true);
        given(examinationRepository.findAll())
                .willReturn(List.of(firstExamination, secondExamination));

        List<ExaminationDTO> result = examinationService.getAllExaminations();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAllExaminationsShouldReturnAllExaminationsForDoctor() {
        Examination firstExamination = createExamination(1L, doctor, patient);
        Examination secondExamination = createExamination(2L, anotherDoctor, anotherPatient);

        given(currentUserService.isAdmin()).willReturn(false);
        given(currentUserService.isDoctor()).willReturn(true);

        given(examinationRepository.findAll())
                .willReturn(List.of(firstExamination, secondExamination));

        List<ExaminationDTO> result = examinationService.getAllExaminations();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getAllExaminationsShouldReturnOnlyOwnExaminationsForPatient() {
        Examination ownExamination = createExamination(1L, doctor, patient);

        given(currentUserService.isAdmin()).willReturn(false);
        given(currentUserService.isDoctor()).willReturn(false);
        given(currentUserService.isPatient()).willReturn(true);
        given(currentUserService.getCurrentPatientId()).willReturn(1L);

        given(examinationRepository.findByPatientId(1L))
                .willReturn(List.of(ownExamination));

        List<ExaminationDTO> result = examinationService.getAllExaminations();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId()).isEqualTo(1L);
    }

    private ExaminationRequestDTO createRequestDTO() {
        ExaminationRequestDTO requestDTO = new ExaminationRequestDTO();
        requestDTO.setExaminationDate(LocalDate.of(2026, 6, 10));
        requestDTO.setDoctorId(1L);
        requestDTO.setPatientId(1L);
        requestDTO.setDiagnosisId(1L);
        requestDTO.setPrice(BigDecimal.valueOf(80.00));

        return requestDTO;
    }

    private Examination createExamination(Long id, Doctor doctor, Patient patient) {
        Examination examination = new Examination();
        examination.setId(id);
        examination.setExaminationDate(LocalDate.of(2026, 6, 10));
        examination.setDoctor(doctor);
        examination.setPatient(patient);
        examination.setDiagnosis(diagnosis);
        examination.setPrice(BigDecimal.valueOf(80.00));
        examination.setPaymentSource(PaymentSource.NHIF);

        return examination;
    }
}