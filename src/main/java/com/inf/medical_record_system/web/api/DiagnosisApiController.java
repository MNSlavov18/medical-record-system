package com.inf.medical_record_system.web.api;

import com.inf.medical_record_system.dto.DiagnosisDTO;
import com.inf.medical_record_system.dto.DiagnosisRequestDTO;
import com.inf.medical_record_system.service.DiagnosisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Diagnoses", description = "Operations for managing medical diagnoses")
@RestController
@RequestMapping("/api/diagnoses")
public class DiagnosisApiController {

    private final DiagnosisService diagnosisService;

    public DiagnosisApiController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @Operation(summary = "Find all diagnoses")
    @GetMapping
    public ResponseEntity<List<DiagnosisDTO>> getAllDiagnoses() {
        return ResponseEntity.ok(diagnosisService.getAllDiagnoses());
    }

    @Operation(summary = "Find diagnosis by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisDTO> getDiagnosisById(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosisService.getDiagnosisById(id));
    }

    @Operation(summary = "Create diagnosis")
    @PostMapping
    public ResponseEntity<DiagnosisDTO> createDiagnosis(@Valid @RequestBody DiagnosisRequestDTO diagnosisRequestDTO) {
        DiagnosisDTO createdDiagnosis = diagnosisService.createDiagnosis(diagnosisRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDiagnosis);
    }

    @Operation(summary = "Update diagnosis")
    @PutMapping("/{id}")
    public ResponseEntity<DiagnosisDTO> updateDiagnosis(
            @PathVariable Long id,
            @Valid @RequestBody DiagnosisRequestDTO diagnosisRequestDTO
    ) {
        return ResponseEntity.ok(diagnosisService.updateDiagnosis(id, diagnosisRequestDTO));
    }

    @Operation(summary = "Delete diagnosis")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
        return ResponseEntity.noContent().build();
    }
}