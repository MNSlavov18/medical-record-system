package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.data.entity.RoleName;
import com.inf.medical_record_system.dto.DoctorDTO;
import com.inf.medical_record_system.dto.PatientDTO;
import com.inf.medical_record_system.dto.PatientRequestDTO;
import com.inf.medical_record_system.dto.UserDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.service.DoctorService;
import com.inf.medical_record_system.service.PatientService;
import com.inf.medical_record_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/patients")
public class PatientViewController {

    private final PatientService patientService;
    private final UserService userService;
    private final DoctorService doctorService;

    public PatientViewController(
            PatientService patientService,
            UserService userService,
            DoctorService doctorService
    ) {
        this.patientService = patientService;
        this.userService = userService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String getAllPatients(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        return "patients/all-patients";
    }

    @GetMapping("/add")
    public String showAddPatientForm(
            @RequestParam(required = false) String doctorName,
            Model model
    ) {
        if (!model.containsAttribute("patient")) {
            model.addAttribute("patient", new PatientRequestDTO());
        }

        addFormAttributes(model, doctorName);

        return "patients/add-patient";
    }

    @PostMapping("/add")
    public String addPatient(
            @Valid @ModelAttribute("patient") PatientRequestDTO patientRequestDTO,
            BindingResult bindingResult,
            @RequestParam(required = false) String doctorName,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, doctorName);
            return "patients/add-patient";
        }

        try {
            patientService.createPatient(patientRequestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model, doctorName);
            return "patients/add-patient";
        }

        return "redirect:/patients";
    }

    @GetMapping("/edit/{id}")
    public String showEditPatientForm(
            @PathVariable Long id,
            @RequestParam(required = false) String doctorName,
            Model model
    ) {
        PatientDTO patientDTO = patientService.getPatientById(id);

        PatientRequestDTO requestDTO = new PatientRequestDTO();
        requestDTO.setFullName(patientDTO.getFullName());
        requestDTO.setEgn(patientDTO.getEgn());
        requestDTO.setUserId(patientDTO.getUserId());
        requestDTO.setPersonalDoctorId(patientDTO.getPersonalDoctorId());

        model.addAttribute("patientId", id);
        model.addAttribute("patient", requestDTO);

        addFormAttributes(model, doctorName);

        return "patients/edit-patient";
    }

    @PostMapping("/edit/{id}")
    public String editPatient(
            @PathVariable Long id,
            @Valid @ModelAttribute("patient") PatientRequestDTO patientRequestDTO,
            BindingResult bindingResult,
            @RequestParam(required = false) String doctorName,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patientId", id);
            addFormAttributes(model, doctorName);
            return "patients/edit-patient";
        }

        try {
            patientService.updatePatient(id, patientRequestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("patientId", id);
            model.addAttribute("formError", exception.getMessage());
            addFormAttributes(model, doctorName);
            return "patients/edit-patient";
        }

        return "redirect:/patients";
    }

    @PostMapping("/delete/{id}")
    public String deletePatient(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            patientService.deletePatient(id);
        } catch (InvalidOperationException exception) {
            redirectAttributes.addFlashAttribute("pageError", exception.getMessage());
        }

        return "redirect:/patients";
    }

    private void addFormAttributes(Model model, String doctorName) {
        List<UserDTO> patientUsers = userService.getAllUsers()
                .stream()
                .filter(user -> user.getRoleName() == RoleName.PATIENT)
                .toList();

        List<DoctorDTO> personalDoctors;

        if (doctorName != null && !doctorName.isBlank()) {
            personalDoctors = doctorService.searchDoctorsByName(doctorName)
                    .stream()
                    .filter(DoctorDTO::isCanBePersonalDoctor)
                    .toList();
        } else {
            personalDoctors = doctorService.getPersonalDoctors();
        }

        model.addAttribute("patientUsers", patientUsers);
        model.addAttribute("personalDoctors", personalDoctors);
        model.addAttribute("doctorName", doctorName);
    }
}