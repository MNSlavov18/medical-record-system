package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {

    List<PatientDTO> getPatientsByDiagnosis(Long diagnosisId);

    DiagnosisCountDTO getMostCommonDiagnosis();

    List<PatientDTO> getPatientsByPersonalDoctor(Long doctorId);

    BigDecimal getTotalPatientPaidExaminationValue();

    DoctorRevenueDTO getPatientPaidExaminationValueByDoctor(Long doctorId);

    List<DoctorPatientCountDTO> getPatientCountByPersonalDoctor();

    List<DoctorVisitCountDTO> getVisitCountByDoctor();

    List<ExaminationDTO> getPatientVisitHistory(Long patientId);

    List<ExaminationDTO> searchExaminations(Long doctorId, LocalDate startDate, LocalDate endDate);

    MonthSickLeaveCountDTO getMonthWithMostSickLeaves();

    List<DoctorSickLeaveCountDTO> getDoctorsWithMostSickLeaves();
}