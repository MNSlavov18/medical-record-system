package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.HealthInsuranceStatus;
import com.inf.medical_record_system.data.entity.Patient;
import com.inf.medical_record_system.data.repo.HealthInsuranceStatusRepository;
import com.inf.medical_record_system.data.repo.PatientRepository;
import com.inf.medical_record_system.dto.HealthInsurancePeriodRequestDTO;
import com.inf.medical_record_system.dto.HealthInsuranceStatusDTO;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.service.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = HealthInsuranceStatusServiceImpl.class)
class HealthInsuranceStatusServiceImplTest {

    @MockBean
    private HealthInsuranceStatusRepository healthInsuranceStatusRepository;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private CurrentUserService currentUserService;

    @Autowired
    private HealthInsuranceStatusServiceImpl healthInsuranceStatusService;

    private Patient patient;

    @BeforeEach
    void init() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFullName("Mario Slavov");
        patient.setEgn("9901011234");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void isPatientInsuredForLastSixMonthsShouldReturnTrueWhenAllSixMonthsAreInsured() {
        Long patientId = 1L;
        LocalDate referenceDate = LocalDate.of(2026, 6, 15);

        given(currentUserService.isAdmin()).willReturn(true);
        given(patientRepository.findById(patientId)).willReturn(Optional.of(patient));

        for (int i = 0; i < 6; i++) {
            YearMonth checkedMonth = YearMonth.from(referenceDate).minusMonths(i);

            given(healthInsuranceStatusRepository.findByPatientIdAndYearAndMonth(
                    patientId,
                    checkedMonth.getYear(),
                    checkedMonth.getMonthValue()
            )).willReturn(Optional.of(createStatus(
                    patient,
                    checkedMonth.getYear(),
                    checkedMonth.getMonthValue(),
                    true
            )));
        }

        boolean result = healthInsuranceStatusService.isPatientInsuredForLastSixMonths(
                patientId,
                referenceDate
        );

        assertThat(result).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void isPatientInsuredForLastSixMonthsShouldReturnFalseWhenOneMonthIsMissing() {
        Long patientId = 1L;
        LocalDate referenceDate = LocalDate.of(2026, 6, 15);

        given(currentUserService.isAdmin()).willReturn(true);
        given(patientRepository.findById(patientId)).willReturn(Optional.of(patient));

        YearMonth checkedMonth = YearMonth.from(referenceDate);

        given(healthInsuranceStatusRepository.findByPatientIdAndYearAndMonth(
                patientId,
                checkedMonth.getYear(),
                checkedMonth.getMonthValue()
        )).willReturn(Optional.empty());

        boolean result = healthInsuranceStatusService.isPatientInsuredForLastSixMonths(
                patientId,
                referenceDate
        );

        assertThat(result).isFalse();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void isPatientInsuredForLastSixMonthsShouldReturnFalseWhenOneMonthIsNotInsured() {
        Long patientId = 1L;
        LocalDate referenceDate = LocalDate.of(2026, 6, 15);

        given(currentUserService.isAdmin()).willReturn(true);
        given(patientRepository.findById(patientId)).willReturn(Optional.of(patient));

        for (int i = 0; i < 6; i++) {
            YearMonth checkedMonth = YearMonth.from(referenceDate).minusMonths(i);
            boolean insured = i != 2;

            given(healthInsuranceStatusRepository.findByPatientIdAndYearAndMonth(
                    patientId,
                    checkedMonth.getYear(),
                    checkedMonth.getMonthValue()
            )).willReturn(Optional.of(createStatus(
                    patient,
                    checkedMonth.getYear(),
                    checkedMonth.getMonthValue(),
                    insured
            )));
        }

        boolean result = healthInsuranceStatusService.isPatientInsuredForLastSixMonths(
                patientId,
                referenceDate
        );

        assertThat(result).isFalse();
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void isPatientInsuredForLastSixMonthsShouldThrowExceptionWhenPatientChecksAnotherPatient() {
        Long requestedPatientId = 2L;

        given(currentUserService.isAdmin()).willReturn(false);
        given(currentUserService.isDoctor()).willReturn(false);
        given(currentUserService.isPatient()).willReturn(true);
        given(currentUserService.getCurrentPatientId()).willReturn(1L);

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> healthInsuranceStatusService.isPatientInsuredForLastSixMonths(
                        requestedPatientId,
                        LocalDate.of(2026, 6, 15)
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("Patients can check only their own insurance status");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStatusesForPeriodShouldCreateStatusesForEveryMonthInPeriod() {
        HealthInsurancePeriodRequestDTO requestDTO = new HealthInsurancePeriodRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setStartYear(2025);
        requestDTO.setStartMonth(12);
        requestDTO.setEndYear(2026);
        requestDTO.setEndMonth(2);
        requestDTO.setInsured(true);

        given(patientRepository.findById(1L)).willReturn(Optional.of(patient));

        given(healthInsuranceStatusRepository.findByPatientIdAndYearAndMonth(
                anyLong(),
                anyInt(),
                anyInt()
        )).willReturn(Optional.empty());

        given(healthInsuranceStatusRepository.save(any(HealthInsuranceStatus.class)))
                .willAnswer(invocation -> {
                    HealthInsuranceStatus status = invocation.getArgument(0);
                    status.setId((long) status.getMonth());
                    return status;
                });

        List<HealthInsuranceStatusDTO> actualStatuses =
                healthInsuranceStatusService.createStatusesForPeriod(requestDTO);

        assertThat(actualStatuses.size()).isEqualTo(3);

        verify(healthInsuranceStatusRepository, times(3))
                .save(any(HealthInsuranceStatus.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStatusesForPeriodShouldUpdateExistingStatusesInPeriod() {
        HealthInsurancePeriodRequestDTO requestDTO = new HealthInsurancePeriodRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setStartYear(2026);
        requestDTO.setStartMonth(1);
        requestDTO.setEndYear(2026);
        requestDTO.setEndMonth(2);
        requestDTO.setInsured(true);

        HealthInsuranceStatus existingJanuaryStatus = createStatus(patient, 2026, 1, false);
        existingJanuaryStatus.setId(10L);

        HealthInsuranceStatus existingFebruaryStatus = createStatus(patient, 2026, 2, false);
        existingFebruaryStatus.setId(11L);

        given(patientRepository.findById(1L)).willReturn(Optional.of(patient));

        given(healthInsuranceStatusRepository.findByPatientIdAndYearAndMonth(1L, 2026, 1))
                .willReturn(Optional.of(existingJanuaryStatus));

        given(healthInsuranceStatusRepository.findByPatientIdAndYearAndMonth(1L, 2026, 2))
                .willReturn(Optional.of(existingFebruaryStatus));

        given(healthInsuranceStatusRepository.save(any(HealthInsuranceStatus.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        List<HealthInsuranceStatusDTO> actualStatuses =
                healthInsuranceStatusService.createStatusesForPeriod(requestDTO);

        assertThat(actualStatuses.size()).isEqualTo(2);
        assertThat(actualStatuses.get(0).isInsured()).isTrue();
        assertThat(actualStatuses.get(1).isInsured()).isTrue();

        verify(healthInsuranceStatusRepository, times(2))
                .save(any(HealthInsuranceStatus.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStatusesForPeriodShouldThrowExceptionWhenStartMonthIsAfterEndMonth() {
        HealthInsurancePeriodRequestDTO requestDTO = new HealthInsurancePeriodRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setStartYear(2026);
        requestDTO.setStartMonth(6);
        requestDTO.setEndYear(2026);
        requestDTO.setEndMonth(1);
        requestDTO.setInsured(true);

        given(patientRepository.findById(1L)).willReturn(Optional.of(patient));

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> healthInsuranceStatusService.createStatusesForPeriod(requestDTO)
        );

        assertThat(exception.getMessage())
                .isEqualTo("Start month cannot be after end month");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStatusesByPatientShouldReturnAllStatusesForPatient() {
        Long patientId = 1L;

        HealthInsuranceStatus january = createStatus(patient, 2026, 1, true);
        january.setId(1L);

        HealthInsuranceStatus february = createStatus(patient, 2026, 2, false);
        february.setId(2L);

        List<HealthInsuranceStatus> expectedStatuses = List.of(january, february);

        given(currentUserService.isPatient()).willReturn(false);
        given(healthInsuranceStatusRepository.findByPatientId(patientId))
                .willReturn(expectedStatuses);

        List<HealthInsuranceStatusDTO> actualStatuses =
                healthInsuranceStatusService.getStatusesByPatient(patientId);

        assertThat(actualStatuses.size()).isEqualTo(2);
        assertThat(actualStatuses.get(0).getMonth()).isEqualTo(1);
        assertThat(actualStatuses.get(1).getMonth()).isEqualTo(2);
    }

    private HealthInsuranceStatus createStatus(
            Patient patient,
            int year,
            int month,
            boolean insured
    ) {
        HealthInsuranceStatus status = new HealthInsuranceStatus();
        status.setPatient(patient);
        status.setYear(year);
        status.setMonth(month);
        status.setInsured(insured);

        return status;
    }
}