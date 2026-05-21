package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.dto.DiagnosisDTO;
import com.inf.medical_record_system.dto.DiagnosisRequestDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.service.DiagnosisService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/diagnoses")
public class DiagnosisViewController {

    private final DiagnosisService diagnosisService;

    public DiagnosisViewController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @GetMapping
    public String getAllDiagnoses(Model model) {
        model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());
        return "diagnoses/all-diagnoses";
    }

    @GetMapping("/add")
    public String showAddDiagnosisForm(Model model) {
        model.addAttribute("diagnosis", new DiagnosisRequestDTO());
        return "diagnoses/add-diagnosis";
    }

    @PostMapping("/add")
    public String addDiagnosis(
            @Valid @ModelAttribute("diagnosis") DiagnosisRequestDTO diagnosisRequestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "diagnoses/add-diagnosis";
        }

        try {
            diagnosisService.createDiagnosis(diagnosisRequestDTO);
        } catch (DuplicateResourceException exception) {
            model.addAttribute("formError", exception.getMessage());
            return "diagnoses/add-diagnosis";
        }

        return "redirect:/diagnoses";
    }

    @GetMapping("/edit/{id}")
    public String showEditDiagnosisForm(@PathVariable Long id, Model model) {
        DiagnosisDTO diagnosisDTO = diagnosisService.getDiagnosisById(id);

        DiagnosisRequestDTO requestDTO = new DiagnosisRequestDTO();
        requestDTO.setCode(diagnosisDTO.getCode());
        requestDTO.setName(diagnosisDTO.getName());
        requestDTO.setDescription(diagnosisDTO.getDescription());

        model.addAttribute("diagnosisId", id);
        model.addAttribute("diagnosis", requestDTO);

        return "diagnoses/edit-diagnosis";
    }

    @PostMapping("/edit/{id}")
    public String editDiagnosis(
            @PathVariable Long id,
            @Valid @ModelAttribute("diagnosis") DiagnosisRequestDTO diagnosisRequestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("diagnosisId", id);
            return "diagnoses/edit-diagnosis";
        }

        try {
            diagnosisService.updateDiagnosis(id, diagnosisRequestDTO);
        } catch (DuplicateResourceException exception) {
            model.addAttribute("diagnosisId", id);
            model.addAttribute("formError", exception.getMessage());
            return "diagnoses/edit-diagnosis";
        }

        return "redirect:/diagnoses";
    }

    @PostMapping("/delete/{id}")
    public String deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
        return "redirect:/diagnoses";
    }
}