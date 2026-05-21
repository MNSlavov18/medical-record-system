package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.data.entity.RoleName;
import com.inf.medical_record_system.dto.DoctorDTO;
import com.inf.medical_record_system.dto.DoctorRequestDTO;
import com.inf.medical_record_system.dto.SpecialtyDTO;
import com.inf.medical_record_system.dto.UserDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.service.DoctorService;
import com.inf.medical_record_system.service.SpecialtyService;
import com.inf.medical_record_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/doctors")
public class DoctorViewController {

    private final DoctorService doctorService;
    private final UserService userService;
    private final SpecialtyService specialtyService;

    public DoctorViewController(
            DoctorService doctorService,
            UserService userService,
            SpecialtyService specialtyService
    ) {
        this.doctorService = doctorService;
        this.userService = userService;
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public String getAllDoctors(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "doctors/all-doctors";
    }

    @GetMapping("/add")
    public String showAddDoctorForm(Model model) {
        model.addAttribute("doctor", new DoctorRequestDTO());
        addFormAttributes(model);

        return "doctors/add-doctor";
    }

    @PostMapping("/add")
    public String addDoctor(
            @Valid @ModelAttribute("doctor") DoctorRequestDTO doctorRequestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "doctors/add-doctor";
        }

        try {
            doctorService.createDoctor(doctorRequestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model);
            return "doctors/add-doctor";
        }

        return "redirect:/doctors";
    }

    @GetMapping("/edit/{id}")
    public String showEditDoctorForm(@PathVariable Long id, Model model) {
        DoctorDTO doctorDTO = doctorService.getDoctorById(id);

        DoctorRequestDTO requestDTO = new DoctorRequestDTO();
        requestDTO.setUniqueIdentificationNumber(doctorDTO.getUniqueIdentificationNumber());
        requestDTO.setFullName(doctorDTO.getFullName());
        requestDTO.setUserId(doctorDTO.getUserId());
        requestDTO.setSpecialtyId(doctorDTO.getSpecialtyId());
        requestDTO.setCanBePersonalDoctor(doctorDTO.isCanBePersonalDoctor());

        model.addAttribute("doctorId", id);
        model.addAttribute("doctor", requestDTO);
        addFormAttributes(model);

        return "doctors/edit-doctor";
    }

    @PostMapping("/edit/{id}")
    public String editDoctor(
            @PathVariable Long id,
            @Valid @ModelAttribute("doctor") DoctorRequestDTO doctorRequestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctorId", id);
            addFormAttributes(model);
            return "doctors/edit-doctor";
        }

        try {
            doctorService.updateDoctor(id, doctorRequestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("doctorId", id);
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model);
            return "doctors/edit-doctor";
        }

        return "redirect:/doctors";
    }

    @PostMapping("/delete/{id}")
    public String deleteDoctor(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            doctorService.deleteDoctor(id);
        } catch (InvalidOperationException exception) {
            redirectAttributes.addFlashAttribute("pageError", exception.getMessage());
        }

        return "redirect:/doctors";
    }

    private void addFormAttributes(Model model) {
        List<UserDTO> doctorUsers = userService.getAllUsers()
                .stream()
                .filter(user -> user.getRoleName() == RoleName.DOCTOR)
                .toList();

        List<SpecialtyDTO> specialties = specialtyService.getAllSpecialties();

        model.addAttribute("doctorUsers", doctorUsers);
        model.addAttribute("specialties", specialties);
    }
}