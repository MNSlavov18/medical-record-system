package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.dto.DiagnosisDTO;
import com.inf.medical_record_system.dto.DoctorDTO;
import com.inf.medical_record_system.dto.ExaminationDTO;
import com.inf.medical_record_system.dto.ExaminationRequestDTO;
import com.inf.medical_record_system.dto.PatientDTO;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.DiagnosisService;
import com.inf.medical_record_system.service.DoctorService;
import com.inf.medical_record_system.service.ExaminationService;
import com.inf.medical_record_system.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/examinations")
public class ExaminationViewController {

    private final ExaminationService examinationService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final CurrentUserService currentUserService;

    public ExaminationViewController(
            ExaminationService examinationService,
            DoctorService doctorService,
            PatientService patientService,
            DiagnosisService diagnosisService,
            CurrentUserService currentUserService
    ) {
        this.examinationService = examinationService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.diagnosisService = diagnosisService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String getAllExaminations(Model model) {
        model.addAttribute("examinations", examinationService.getAllExaminations());
        return "examinations/all-examinations";
    }

    @GetMapping("/add")
    public String showAddExaminationForm(Model model) {
        model.addAttribute("examination", new ExaminationRequestDTO());
        addFormAttributes(model);

        return "examinations/add-examination";
    }

    @PostMapping("/add")
    public String addExamination(
            @Valid @ModelAttribute("examination") ExaminationRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "examinations/add-examination";
        }

        try {
            examinationService.createExamination(requestDTO);
        } catch (InvalidOperationException exception) {
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model);
            return "examinations/add-examination";
        }

        return "redirect:/examinations";
    }

    @GetMapping("/edit/{id}")
    public String showEditExaminationForm(@PathVariable Long id, Model model) {
        ExaminationDTO examinationDTO = examinationService.getExaminationById(id);

        ExaminationRequestDTO requestDTO = new ExaminationRequestDTO();
        requestDTO.setExaminationDate(examinationDTO.getExaminationDate());
        requestDTO.setDoctorId(examinationDTO.getDoctorId());
        requestDTO.setPatientId(examinationDTO.getPatientId());
        requestDTO.setDiagnosisId(examinationDTO.getDiagnosisId());
        requestDTO.setPrice(examinationDTO.getPrice());

        model.addAttribute("examinationId", id);
        model.addAttribute("examination", requestDTO);
        addFormAttributes(model);

        return "examinations/edit-examination";
    }

    @PostMapping("/edit/{id}")
    public String editExamination(
            @PathVariable Long id,
            @Valid @ModelAttribute("examination") ExaminationRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("examinationId", id);
            addFormAttributes(model);
            return "examinations/edit-examination";
        }

        try {
            examinationService.updateExamination(id, requestDTO);
        } catch (InvalidOperationException exception) {
            model.addAttribute("examinationId", id);
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model);
            return "examinations/edit-examination";
        }

        return "redirect:/examinations";
    }

    @PostMapping("/delete/{id}")
    public String deleteExamination(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            examinationService.deleteExamination(id);
        } catch (InvalidOperationException exception) {
            redirectAttributes.addFlashAttribute("pageError", exception.getMessage());
        }

        return "redirect:/examinations";
    }

    private void addFormAttributes(Model model) {
        List<DoctorDTO> doctors;

        if (currentUserService.isDoctor()) {
            Long currentDoctorId = currentUserService.getCurrentDoctorId();
            doctors = List.of(doctorService.getDoctorById(currentDoctorId));
        } else {
            doctors = doctorService.getAllDoctors();
        }

        List<PatientDTO> patients = patientService.getAllPatients();
        List<DiagnosisDTO> diagnoses = diagnosisService.getAllDiagnoses();

        model.addAttribute("doctors", doctors);
        model.addAttribute("patients", patients);
        model.addAttribute("diagnoses", diagnoses);
    }
}