package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.dto.ExaminationDTO;
import com.inf.medical_record_system.dto.SickLeaveDTO;
import com.inf.medical_record_system.dto.SickLeaveRequestDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.ExaminationService;
import com.inf.medical_record_system.service.SickLeaveService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/sick-leaves")
public class SickLeaveViewController {

    private final SickLeaveService sickLeaveService;
    private final ExaminationService examinationService;
    private final CurrentUserService currentUserService;

    public SickLeaveViewController(
            SickLeaveService sickLeaveService,
            ExaminationService examinationService,
            CurrentUserService currentUserService
    ) {
        this.sickLeaveService = sickLeaveService;
        this.examinationService = examinationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String getAllSickLeaves(Model model) {
        model.addAttribute("sickLeaves", sickLeaveService.getAllSickLeaves());

        if (currentUserService.isDoctor()) {
            model.addAttribute("currentDoctorId", currentUserService.getCurrentDoctorId());
        }

        model.addAttribute("isAdmin", currentUserService.isAdmin());

        return "sick-leaves/all-sick-leaves";
    }

    @GetMapping("/add")
    public String showAddSickLeaveForm(Model model) {
        model.addAttribute("sickLeave", new SickLeaveRequestDTO());
        addFormAttributes(model);

        return "sick-leaves/add-sick-leave";
    }

    @PostMapping("/add")
    public String addSickLeave(
            @Valid @ModelAttribute("sickLeave") SickLeaveRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "sick-leaves/add-sick-leave";
        }

        try {
            sickLeaveService.createSickLeave(requestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model);
            return "sick-leaves/add-sick-leave";
        }

        return "redirect:/sick-leaves";
    }

    @GetMapping("/edit/{id}")
    public String showEditSickLeaveForm(@PathVariable Long id, Model model) {
        SickLeaveDTO sickLeaveDTO = sickLeaveService.getSickLeaveById(id);

        SickLeaveRequestDTO requestDTO = new SickLeaveRequestDTO();
        requestDTO.setStartDate(sickLeaveDTO.getStartDate());
        requestDTO.setNumberOfDays(sickLeaveDTO.getNumberOfDays());
        requestDTO.setExaminationId(sickLeaveDTO.getExaminationId());

        model.addAttribute("sickLeaveId", id);
        model.addAttribute("sickLeave", requestDTO);
        addFormAttributes(model);

        return "sick-leaves/edit-sick-leave";
    }

    @PostMapping("/edit/{id}")
    public String editSickLeave(
            @PathVariable Long id,
            @Valid @ModelAttribute("sickLeave") SickLeaveRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sickLeaveId", id);
            addFormAttributes(model);
            return "sick-leaves/edit-sick-leave";
        }

        try {
            sickLeaveService.updateSickLeave(id, requestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("sickLeaveId", id);
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model);
            return "sick-leaves/edit-sick-leave";
        }

        return "redirect:/sick-leaves";
    }

    @PostMapping("/delete/{id}")
    public String deleteSickLeave(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            sickLeaveService.deleteSickLeave(id);
        } catch (InvalidOperationException exception) {
            redirectAttributes.addFlashAttribute("pageError", exception.getMessage());
        }

        return "redirect:/sick-leaves";
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