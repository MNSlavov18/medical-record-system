package com.inf.medical_record_system.web.api;

import com.inf.medical_record_system.dto.*;
import com.inf.medical_record_system.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Statistics", description = "Reports and statistics for the medical record system")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsApiController {

    private final StatisticsService statisticsService;

    public StatisticsApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "Find patients by diagnosis")
    @GetMapping("/patients/by-diagnosis/{diagnosisId}")
    public ResponseEntity<List<PatientDTO>> getPatientsByDiagnosis(@PathVariable Long diagnosisId) {
        return ResponseEntity.ok(statisticsService.getPatientsByDiagnosis(diagnosisId));
    }

    @Operation(summary = "Find most common diagnosis")
    @GetMapping("/diagnoses/most-common")
    public ResponseEntity<DiagnosisCountDTO> getMostCommonDiagnosis() {
        return ResponseEntity.ok(statisticsService.getMostCommonDiagnosis());
    }

    @Operation(summary = "Find patients by personal doctor")
    @GetMapping("/patients/by-personal-doctor/{doctorId}")
    public ResponseEntity<List<PatientDTO>> getPatientsByPersonalDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(statisticsService.getPatientsByPersonalDoctor(doctorId));
    }

    @Operation(summary = "Calculate total value of examinations paid by patients")
    @GetMapping("/examinations/patient-paid-total")
    public ResponseEntity<BigDecimal> getTotalPatientPaidExaminationValue() {
        return ResponseEntity.ok(statisticsService.getTotalPatientPaidExaminationValue());
    }

    @Operation(summary = "Calculate value of patient-paid examinations by doctor")
    @GetMapping("/examinations/patient-paid-by-doctor/{doctorId}")
    public ResponseEntity<DoctorRevenueDTO> getPatientPaidExaminationValueByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(statisticsService.getPatientPaidExaminationValueByDoctor(doctorId));
    }

    @Operation(summary = "Count patients by personal doctor")
    @GetMapping("/personal-doctors/patient-count")
    public ResponseEntity<List<DoctorPatientCountDTO>> getPatientCountByPersonalDoctor() {
        return ResponseEntity.ok(statisticsService.getPatientCountByPersonalDoctor());
    }

    @Operation(summary = "Count visits by doctor")
    @GetMapping("/doctors/visit-count")
    public ResponseEntity<List<DoctorVisitCountDTO>> getVisitCountByDoctor() {
        return ResponseEntity.ok(statisticsService.getVisitCountByDoctor());
    }

    @Operation(summary = "Find patient visit history")
    @GetMapping("/patients/{patientId}/visit-history")
    public ResponseEntity<List<ExaminationDTO>> getPatientVisitHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(statisticsService.getPatientVisitHistory(patientId));
    }

    @Operation(summary = "Search examinations by doctor and/or period")
    @GetMapping("/examinations/search")
    public ResponseEntity<List<ExaminationDTO>> searchExaminations(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(statisticsService.searchExaminations(doctorId, startDate, endDate));
    }

    @Operation(summary = "Find month with most sick leaves")
    @GetMapping("/sick-leaves/month-with-most")
    public ResponseEntity<MonthSickLeaveCountDTO> getMonthWithMostSickLeaves() {
        return ResponseEntity.ok(statisticsService.getMonthWithMostSickLeaves());
    }

    @Operation(summary = "Find doctor or doctors with most sick leaves")
    @GetMapping("/sick-leaves/doctors-with-most")
    public ResponseEntity<List<DoctorSickLeaveCountDTO>> getDoctorsWithMostSickLeaves() {
        return ResponseEntity.ok(statisticsService.getDoctorsWithMostSickLeaves());
    }
}