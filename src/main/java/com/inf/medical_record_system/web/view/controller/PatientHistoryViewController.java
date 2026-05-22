package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.service.ExaminationService;
import com.inf.medical_record_system.service.PatientService;
import com.inf.medical_record_system.service.SickLeaveService;
import com.inf.medical_record_system.service.TreatmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
public class PatientHistoryViewController {

    private final PatientService patientService;
    private final ExaminationService examinationService;
    private final TreatmentService treatmentService;
    private final SickLeaveService sickLeaveService;

    public PatientHistoryViewController(
            PatientService patientService,
            ExaminationService examinationService,
            TreatmentService treatmentService,
            SickLeaveService sickLeaveService
    ) {
        this.patientService = patientService;
        this.examinationService = examinationService;
        this.treatmentService = treatmentService;
        this.sickLeaveService = sickLeaveService;
    }

    @GetMapping("/{id}/history")
    public String getPatientHistory(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        model.addAttribute("examinations", examinationService.getExaminationsByPatient(id));
        model.addAttribute("treatments", treatmentService.getTreatmentsByPatient(id));
        model.addAttribute("sickLeaves", sickLeaveService.getSickLeavesByPatient(id));

        return "patients/patient-history";
    }
}