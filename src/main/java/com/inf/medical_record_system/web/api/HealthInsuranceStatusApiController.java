package com.inf.medical_record_system.web.api;

import com.inf.medical_record_system.dto.HealthInsuranceStatusDTO;
import com.inf.medical_record_system.dto.HealthInsuranceStatusRequestDTO;
import com.inf.medical_record_system.service.HealthInsuranceStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Health Insurance Statuses", description = "Operations for managing patient health insurance status")
@RestController
@RequestMapping("/api/health-insurance-statuses")
public class HealthInsuranceStatusApiController {

    private final HealthInsuranceStatusService healthInsuranceStatusService;

    public HealthInsuranceStatusApiController(HealthInsuranceStatusService healthInsuranceStatusService) {
        this.healthInsuranceStatusService = healthInsuranceStatusService;
    }

    @Operation(summary = "Find all health insurance statuses")
    @GetMapping
    public ResponseEntity<List<HealthInsuranceStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(healthInsuranceStatusService.getAllStatuses());
    }

    @Operation(summary = "Find health insurance status by ID")
    @GetMapping("/{id}")
    public ResponseEntity<HealthInsuranceStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(healthInsuranceStatusService.getStatusById(id));
    }

    @Operation(summary = "Find health insurance statuses by patient")
    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<HealthInsuranceStatusDTO>> getStatusesByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(healthInsuranceStatusService.getStatusesByPatient(patientId));
    }

    @Operation(summary = "Check if patient is insured for last 6 months")
    @GetMapping("/patient/{patientId}/insured-last-six-months")
    public ResponseEntity<Boolean> isPatientInsuredForLastSixMonths(@PathVariable Long patientId) {
        return ResponseEntity.ok(
                healthInsuranceStatusService.isPatientInsuredForLastSixMonths(patientId, LocalDate.now())
        );
    }

    @Operation(summary = "Create health insurance status")
    @PostMapping
    public ResponseEntity<HealthInsuranceStatusDTO> createStatus(
            @Valid @RequestBody HealthInsuranceStatusRequestDTO requestDTO
    ) {
        HealthInsuranceStatusDTO createdStatus = healthInsuranceStatusService.createStatus(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @Operation(summary = "Update health insurance status")
    @PutMapping("/{id}")
    public ResponseEntity<HealthInsuranceStatusDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody HealthInsuranceStatusRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(healthInsuranceStatusService.updateStatus(id, requestDTO));
    }

    @Operation(summary = "Delete health insurance status")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        healthInsuranceStatusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}