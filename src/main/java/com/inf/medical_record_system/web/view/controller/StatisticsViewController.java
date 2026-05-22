package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.dto.ExaminationDTO;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.DiagnosisService;
import com.inf.medical_record_system.service.DoctorService;
import com.inf.medical_record_system.service.PatientService;
import com.inf.medical_record_system.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/statistics")
public class StatisticsViewController {

    private final StatisticsService statisticsService;
    private final DiagnosisService diagnosisService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final CurrentUserService currentUserService;

    public StatisticsViewController(
            StatisticsService statisticsService,
            DiagnosisService diagnosisService,
            DoctorService doctorService,
            PatientService patientService,
            CurrentUserService currentUserService
    ) {
        this.statisticsService = statisticsService;
        this.diagnosisService = diagnosisService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String getStatisticsPage(
            @RequestParam(required = false) Long diagnosisId,
            @RequestParam(required = false) Long personalDoctorId,
            @RequestParam(required = false) Long patientHistoryId,
            @RequestParam(required = false) Long revenueDoctorId,
            @RequestParam(required = false) Long searchDoctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ) {
        addBaseData(model);

        addGeneralStatistics(model);

        if (diagnosisId != null) {
            model.addAttribute("selectedDiagnosisId", diagnosisId);
            model.addAttribute("patientsByDiagnosis", statisticsService.getPatientsByDiagnosis(diagnosisId));
        }

        if (personalDoctorId != null) {
            model.addAttribute("selectedPersonalDoctorId", personalDoctorId);
            model.addAttribute("patientsByPersonalDoctor", statisticsService.getPatientsByPersonalDoctor(personalDoctorId));
        }

        if (patientHistoryId != null) {
            model.addAttribute("selectedPatientHistoryId", patientHistoryId);
            model.addAttribute("patientHistory", statisticsService.getPatientVisitHistory(patientHistoryId));
        }

        if (currentUserService.isAdmin() && revenueDoctorId != null) {
            model.addAttribute("selectedRevenueDoctorId", revenueDoctorId);
            model.addAttribute("doctorRevenue", statisticsService.getPatientPaidExaminationValueByDoctor(revenueDoctorId));
        }

        if (searchDoctorId != null || startDate != null || endDate != null) {
            model.addAttribute("selectedSearchDoctorId", searchDoctorId);
            model.addAttribute("selectedStartDate", startDate);
            model.addAttribute("selectedEndDate", endDate);

            try {
                List<ExaminationDTO> searchResults = statisticsService.searchExaminations(
                        searchDoctorId,
                        startDate,
                        endDate
                );

                model.addAttribute("searchResults", searchResults);
            } catch (InvalidOperationException exception) {
                model.addAttribute("searchError", exception.getMessage());
            }
        }

        return "statistics/statistics";
    }

    private void addBaseData(Model model) {
        model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("isAdmin", currentUserService.isAdmin());
    }

    private void addGeneralStatistics(Model model) {
        try {
            model.addAttribute("mostCommonDiagnosis", statisticsService.getMostCommonDiagnosis());
        } catch (ResourceNotFoundException exception) {
            model.addAttribute("mostCommonDiagnosisError", exception.getMessage());
        }

        model.addAttribute("visitCounts", statisticsService.getVisitCountByDoctor());

        try {
            model.addAttribute("monthWithMostSickLeaves", statisticsService.getMonthWithMostSickLeaves());
        } catch (ResourceNotFoundException exception) {
            model.addAttribute("monthWithMostSickLeavesError", exception.getMessage());
        }

        try {
            model.addAttribute("doctorsWithMostSickLeaves", statisticsService.getDoctorsWithMostSickLeaves());
        } catch (ResourceNotFoundException exception) {
            model.addAttribute("doctorsWithMostSickLeavesError", exception.getMessage());
        }

        if (currentUserService.isAdmin()) {
            model.addAttribute("totalPatientPaid", statisticsService.getTotalPatientPaidExaminationValue());
            model.addAttribute("patientCounts", statisticsService.getPatientCountByPersonalDoctor());
        }
    }
}