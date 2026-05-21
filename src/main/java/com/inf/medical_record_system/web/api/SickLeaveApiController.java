package com.inf.medical_record_system.web.api;

import com.inf.medical_record_system.dto.SickLeaveDTO;
import com.inf.medical_record_system.dto.SickLeaveRequestDTO;
import com.inf.medical_record_system.service.SickLeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Sick Leaves", description = "Operations for managing sick leaves")
@RestController
@RequestMapping("/api/sick-leaves")
public class SickLeaveApiController {

    private final SickLeaveService sickLeaveService;

    public SickLeaveApiController(SickLeaveService sickLeaveService) {
        this.sickLeaveService = sickLeaveService;
    }

    @Operation(summary = "Find all sick leaves")
    @GetMapping
    public ResponseEntity<List<SickLeaveDTO>> getAllSickLeaves() {
        return ResponseEntity.ok(sickLeaveService.getAllSickLeaves());
    }

    @Operation(summary = "Find sick leave by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SickLeaveDTO> getSickLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(sickLeaveService.getSickLeaveById(id));
    }

    @Operation(summary = "Find sick leave by examination")
    @GetMapping("/by-examination/{examinationId}")
    public ResponseEntity<SickLeaveDTO> getSickLeaveByExamination(@PathVariable Long examinationId) {
        return ResponseEntity.ok(sickLeaveService.getSickLeaveByExamination(examinationId));
    }

    @Operation(summary = "Find sick leaves by patient")
    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<SickLeaveDTO>> getSickLeavesByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(sickLeaveService.getSickLeavesByPatient(patientId));
    }

    @Operation(summary = "Find sick leaves by doctor")
    @GetMapping("/by-doctor/{doctorId}")
    public ResponseEntity<List<SickLeaveDTO>> getSickLeavesByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(sickLeaveService.getSickLeavesByDoctor(doctorId));
    }

    @Operation(summary = "Find sick leaves by period")
    @GetMapping("/by-period")
    public ResponseEntity<List<SickLeaveDTO>> getSickLeavesByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(sickLeaveService.getSickLeavesByPeriod(startDate, endDate));
    }

    @Operation(summary = "Create sick leave")
    @PostMapping
    public ResponseEntity<SickLeaveDTO> createSickLeave(@Valid @RequestBody SickLeaveRequestDTO requestDTO) {
        SickLeaveDTO createdSickLeave = sickLeaveService.createSickLeave(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSickLeave);
    }

    @Operation(summary = "Update sick leave")
    @PutMapping("/{id}")
    public ResponseEntity<SickLeaveDTO> updateSickLeave(
            @PathVariable Long id,
            @Valid @RequestBody SickLeaveRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(sickLeaveService.updateSickLeave(id, requestDTO));
    }

    @Operation(summary = "Delete sick leave")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSickLeave(@PathVariable Long id) {
        sickLeaveService.deleteSickLeave(id);
        return ResponseEntity.noContent().build();
    }
}