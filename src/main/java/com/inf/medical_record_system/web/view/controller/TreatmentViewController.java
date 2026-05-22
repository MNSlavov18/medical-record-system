package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.dto.ExaminationDTO;
import com.inf.medical_record_system.dto.TreatmentDTO;
import com.inf.medical_record_system.dto.TreatmentRequestDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.ExaminationService;
import com.inf.medical_record_system.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/treatments")
public class TreatmentViewController {

    private final TreatmentService treatmentService;
    private final ExaminationService examinationService;
    private final CurrentUserService currentUserService;

    public TreatmentViewController(
            TreatmentService treatmentService,
            ExaminationService examinationService,
            CurrentUserService currentUserService
    ) {
        this.treatmentService = treatmentService;
        this.examinationService = examinationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String getAllTreatments(Model model) {
        model.addAttribute("treatments", treatmentService.getAllTreatments());

        if (currentUserService.isDoctor()) {
            model.addAttribute("currentDoctorId", currentUserService.getCurrentDoctorId());
        }

        model.addAttribute("isAdmin", currentUserService.isAdmin());

        return "treatments/all-treatments";
    }

    @GetMapping("/add")
    public String showAddTreatmentForm(Model model) {
        model.addAttribute("treatment", new TreatmentRequestDTO());
        addFormAttributes(model);

        return "treatments/add-treatment";
    }

    @PostMapping("/add")
    public String addTreatment(
            @Valid @ModelAttribute("treatment") TreatmentRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "treatments/add-treatment";
        }

        try {
            treatmentService.createTreatment(requestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model);
            return "treatments/add-treatment";
        }

        return "redirect:/treatments";
    }

    @GetMapping("/edit/{id}")
    public String showEditTreatmentForm(@PathVariable Long id, Model model) {
        TreatmentDTO treatmentDTO = treatmentService.getTreatmentById(id);

        TreatmentRequestDTO requestDTO = new TreatmentRequestDTO();
        requestDTO.setDescription(treatmentDTO.getDescription());
        requestDTO.setStartDate(treatmentDTO.getStartDate());
        requestDTO.setEndDate(treatmentDTO.getEndDate());
        requestDTO.setPrescribedMedication(treatmentDTO.getPrescribedMedication());
        requestDTO.setExaminationId(treatmentDTO.getExaminationId());

        model.addAttribute("treatmentId", id);
        model.addAttribute("treatment", requestDTO);
        addFormAttributes(model);

        return "treatments/edit-treatment";
    }

    @PostMapping("/edit/{id}")
    public String editTreatment(
            @PathVariable Long id,
            @Valid @ModelAttribute("treatment") TreatmentRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("treatmentId", id);
            addFormAttributes(model);
            return "treatments/edit-treatment";
        }

        try {
            treatmentService.updateTreatment(id, requestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("treatmentId", id);
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model);
            return "treatments/edit-treatment";
        }

        return "redirect:/treatments";
    }

    @PostMapping("/delete/{id}")
    public String deleteTreatment(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            treatmentService.deleteTreatment(id);
        } catch (InvalidOperationException exception) {
            redirectAttributes.addFlashAttribute("pageError", exception.getMessage());
        }

        return "redirect:/treatments";
    }

    private void addFormAttributes(Model model) {
        List<ExaminationDTO> examinations = examinationService.getAllExaminations();

        if (currentUserService.isDoctor()) {
            Long currentDoctorId = currentUserService.getCurrentDoctorId();

            examinations = examinations.stream()
                    .filter(examination -> examination.getDoctorId().equals(currentDoctorId))
                    .toList();
        }

        model.addAttribute("examinations", examinations);
    }
}