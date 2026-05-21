package com.inf.medical_record_system.web.api;

import com.inf.medical_record_system.dto.ExaminationDTO;
import com.inf.medical_record_system.dto.ExaminationRequestDTO;
import com.inf.medical_record_system.service.ExaminationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Examinations", description = "Operations for managing patient examinations")
@RestController
@RequestMapping("/api/examinations")
public class ExaminationApiController {

    private final ExaminationService examinationService;

    public ExaminationApiController(ExaminationService examinationService) {
        this.examinationService = examinationService;
    }

    @Operation(summary = "Find all examinations")
    @GetMapping
    public ResponseEntity<List<ExaminationDTO>> getAllExaminations() {
        return ResponseEntity.ok(examinationService.getAllExaminations());
    }

    @Operation(summary = "Find examination by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ExaminationDTO> getExaminationById(@PathVariable Long id) {
        return ResponseEntity.ok(examinationService.getExaminationById(id));
    }

    @Operation(summary = "Find examinations by patient")
    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<ExaminationDTO>> getExaminationsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(examinationService.getExaminationsByPatient(patientId));
    }

    @Operation(summary = "Find examinations by doctor")
    @GetMapping("/by-doctor/{doctorId}")
    public ResponseEntity<List<ExaminationDTO>> getExaminationsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(examinationService.getExaminationsByDoctor(doctorId));
    }

    @Operation(summary = "Find examinations by diagnosis")
    @GetMapping("/by-diagnosis/{diagnosisId}")
    public ResponseEntity<List<ExaminationDTO>> getExaminationsByDiagnosis(@PathVariable Long diagnosisId) {
        return ResponseEntity.ok(examinationService.getExaminationsByDiagnosis(diagnosisId));
    }

    @Operation(summary = "Find examinations by period")
    @GetMapping("/by-period")
    public ResponseEntity<List<ExaminationDTO>> getExaminationsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(examinationService.getExaminationsByPeriod(startDate, endDate));
    }

    @Operation(summary = "Find examinations by doctor and period")
    @GetMapping("/by-doctor/{doctorId}/by-period")
    public ResponseEntity<List<ExaminationDTO>> getExaminationsByDoctorAndPeriod(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(examinationService.getExaminationsByDoctorAndPeriod(doctorId, startDate, endDate));
    }

    @Operation(summary = "Create examination")
    @PostMapping
    public ResponseEntity<ExaminationDTO> createExamination(
            @Valid @RequestBody ExaminationRequestDTO requestDTO
    ) {
        ExaminationDTO createdExamination = examinationService.createExamination(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExamination);
    }

    @Operation(summary = "Update examination")
    @PutMapping("/{id}")
    public ResponseEntity<ExaminationDTO> updateExamination(
            @PathVariable Long id,
            @Valid @RequestBody ExaminationRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(examinationService.updateExamination(id, requestDTO));
    }

    @Operation(summary = "Delete examination")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamination(@PathVariable Long id) {
        examinationService.deleteExamination(id);
        return ResponseEntity.noContent().build();
    }
}