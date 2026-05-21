package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.dto.SpecialtyDTO;
import com.inf.medical_record_system.dto.SpecialtyRequestDTO;
import com.inf.medical_record_system.service.SpecialtyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/specialties")
public class SpecialtyViewController {

    private final SpecialtyService specialtyService;

    public SpecialtyViewController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public String getAllSpecialties(Model model) {
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        return "specialties/all-specialties";
    }

    @GetMapping("/add")
    public String showAddSpecialtyForm(Model model) {
        model.addAttribute("specialty", new SpecialtyRequestDTO());
        return "specialties/add-specialty";
    }

    @PostMapping("/add")
    public String addSpecialty(
            @Valid @ModelAttribute("specialty") SpecialtyRequestDTO specialtyRequestDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "specialties/add-specialty";
        }

        specialtyService.createSpecialty(specialtyRequestDTO);
        return "redirect:/specialties";
    }

    @GetMapping("/edit/{id}")
    public String showEditSpecialtyForm(@PathVariable Long id, Model model) {
        SpecialtyDTO specialtyDTO = specialtyService.getSpecialtyById(id);

        SpecialtyRequestDTO requestDTO = new SpecialtyRequestDTO();
        requestDTO.setName(specialtyDTO.getName());

        model.addAttribute("specialtyId", id);
        model.addAttribute("specialty", requestDTO);

        return "specialties/edit-specialty";
    }

    @PostMapping("/edit/{id}")
    public String editSpecialty(
            @PathVariable Long id,
            @Valid @ModelAttribute("specialty") SpecialtyRequestDTO specialtyRequestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("specialtyId", id);
            return "specialties/edit-specialty";
        }

        specialtyService.updateSpecialty(id, specialtyRequestDTO);
        return "redirect:/specialties";
    }

    @PostMapping("/delete/{id}")
    public String deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        return "redirect:/specialties";
    }
}