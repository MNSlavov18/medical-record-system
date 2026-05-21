package com.inf.medical_record_system.web.api;

import com.inf.medical_record_system.dto.TreatmentDTO;
import com.inf.medical_record_system.dto.TreatmentRequestDTO;
import com.inf.medical_record_system.service.TreatmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Treatments", description = "Operations for managing treatments")
@RestController
@RequestMapping("/api/treatments")
public class TreatmentApiController {

    private final TreatmentService treatmentService;

    public TreatmentApiController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @Operation(summary = "Find all treatments")
    @GetMapping
    public ResponseEntity<List<TreatmentDTO>> getAllTreatments() {
        return ResponseEntity.ok(treatmentService.getAllTreatments());
    }

    @Operation(summary = "Find treatment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TreatmentDTO> getTreatmentById(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentService.getTreatmentById(id));
    }

    @Operation(summary = "Find treatment by examination")
    @GetMapping("/by-examination/{examinationId}")
    public ResponseEntity<TreatmentDTO> getTreatmentByExamination(@PathVariable Long examinationId) {
        return ResponseEntity.ok(treatmentService.getTreatmentByExamination(examinationId));
    }

    @Operation(summary = "Create treatment")
    @PostMapping
    public ResponseEntity<TreatmentDTO> createTreatment(@Valid @RequestBody TreatmentRequestDTO requestDTO) {
        TreatmentDTO createdTreatment = treatmentService.createTreatment(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTreatment);
    }

    @Operation(summary = "Update treatment")
    @PutMapping("/{id}")
    public ResponseEntity<TreatmentDTO> updateTreatment(
            @PathVariable Long id,
            @Valid @RequestBody TreatmentRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(treatmentService.updateTreatment(id, requestDTO));
    }

    @Operation(summary = "Delete treatment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTreatment(@PathVariable Long id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }
}