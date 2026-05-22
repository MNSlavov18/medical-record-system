package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.dto.HealthInsuranceStatusDTO;
import com.inf.medical_record_system.dto.HealthInsuranceStatusRequestDTO;
import com.inf.medical_record_system.dto.PatientDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.HealthInsuranceStatusService;
import com.inf.medical_record_system.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.inf.medical_record_system.dto.HealthInsurancePeriodRequestDTO;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/health-insurance-statuses")
public class HealthInsuranceStatusViewController {

    private final HealthInsuranceStatusService healthInsuranceStatusService;
    private final PatientService patientService;
    private final CurrentUserService currentUserService;

    public HealthInsuranceStatusViewController(
            HealthInsuranceStatusService healthInsuranceStatusService,
            PatientService patientService,
            CurrentUserService currentUserService
    ) {
        this.healthInsuranceStatusService = healthInsuranceStatusService;
        this.patientService = patientService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String getAllStatuses(Model model) {
        model.addAttribute("statuses", healthInsuranceStatusService.getAllStatuses());

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            boolean insuredLastSixMonths = healthInsuranceStatusService.isPatientInsuredForLastSixMonths(
                    currentPatientId,
                    LocalDate.now()
            );

            model.addAttribute("insuredLastSixMonths", insuredLastSixMonths);
        }

        return "health-insurance-statuses/all-health-insurance-statuses";
    }

    @GetMapping("/add")
    public String showAddStatusForm(Model model) {
        model.addAttribute("statusPeriod", new HealthInsurancePeriodRequestDTO());
        addPatientsToModel(model);

        return "health-insurance-statuses/add-health-insurance-status";
    }

    @PostMapping("/add")
    public String addStatus(
            @Valid @ModelAttribute("statusPeriod") HealthInsurancePeriodRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addPatientsToModel(model);
            return "health-insurance-statuses/add-health-insurance-status";
        }

        try {
            healthInsuranceStatusService.createStatusesForPeriod(requestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("formError", exception.getMessage());
            addPatientsToModel(model);
            return "health-insurance-statuses/add-health-insurance-status";
        }

        return "redirect:/health-insurance-statuses";
    }

    @GetMapping("/edit/{id}")
    public String showEditStatusForm(@PathVariable Long id, Model model) {
        HealthInsuranceStatusDTO statusDTO = healthInsuranceStatusService.getStatusById(id);

        HealthInsuranceStatusRequestDTO requestDTO = new HealthInsuranceStatusRequestDTO();
        requestDTO.setPatientId(statusDTO.getPatientId());
        requestDTO.setYear(statusDTO.getYear());
        requestDTO.setMonth(statusDTO.getMonth());
        requestDTO.setInsured(statusDTO.isInsured());

        model.addAttribute("statusId", id);
        model.addAttribute("status", requestDTO);
        addPatientsToModel(model);

        return "health-insurance-statuses/edit-health-insurance-status";
    }

    @PostMapping("/edit/{id}")
    public String editStatus(
            @PathVariable Long id,
            @Valid @ModelAttribute("status") HealthInsuranceStatusRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statusId", id);
            addPatientsToModel(model);
            return "health-insurance-statuses/edit-health-insurance-status";
        }

        try {
            healthInsuranceStatusService.updateStatus(id, requestDTO);
        } catch (DuplicateResourceException | InvalidOperationException exception) {
            model.addAttribute("statusId", id);
            model.addAttribute("formError", exception.getMessage());
            addPatientsToModel(model);
            return "health-insurance-statuses/edit-health-insurance-status";
        }

        return "redirect:/health-insurance-statuses";
    }

    @PostMapping("/delete/{id}")
    public String deleteStatus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            healthInsuranceStatusService.deleteStatus(id);
        } catch (InvalidOperationException exception) {
            redirectAttributes.addFlashAttribute("pageError", exception.getMessage());
        }

        return "redirect:/health-insurance-statuses";
    }

    @GetMapping("/check")
    public String showInsuranceCheck(
            @RequestParam(required = false) Long patientId,
            Model model
    ) {
        if (currentUserService.isPatient()) {
            return "redirect:/health-insurance-statuses?check=true";
        }

        addPatientsToModel(model);
        model.addAttribute("selectedPatientId", patientId);

        if (patientId != null) {
            boolean insured = healthInsuranceStatusService.isPatientInsuredForLastSixMonths(patientId, LocalDate.now());
            model.addAttribute("insuredResult", insured);
        }

        return "health-insurance-statuses/check-health-insurance-status";
    }

    @GetMapping("/check-my-insurance")
    public String checkMyInsurance() {
        if (!currentUserService.isPatient()) {
            return "redirect:/health-insurance-statuses/check";
        }

        Long currentPatientId = currentUserService.getCurrentPatientId();

        boolean insured = healthInsuranceStatusService.isPatientInsuredForLastSixMonths(
                currentPatientId,
                LocalDate.now()
        );

        if (insured) {
            return "redirect:/health-insurance-statuses?insuranceResult=insured";
        }

        return "redirect:/health-insurance-statuses?insuranceResult=not_insured";
    }

    private void addPatientsToModel(Model model) {
        List<PatientDTO> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
    }
}