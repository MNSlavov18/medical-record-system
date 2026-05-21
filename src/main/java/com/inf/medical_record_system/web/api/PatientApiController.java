package com.inf.medical_record_system.web.api;

import com.inf.medical_record_system.dto.PatientDTO;
import com.inf.medical_record_system.dto.PatientRequestDTO;
import com.inf.medical_record_system.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Patients", description = "Operations for managing patients")
@RestController
@RequestMapping("/api/patients")
public class PatientApiController {

    private final PatientService patientService;

    public PatientApiController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "Find all patients")
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @Operation(summary = "Find patient by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @Operation(summary = "Find patient by EGN")
    @GetMapping("/by-egn/{egn}")
    public ResponseEntity<PatientDTO> getPatientByEgn(@PathVariable String egn) {
        return ResponseEntity.ok(patientService.getPatientByEgn(egn));
    }

    @Operation(summary = "Find patients by personal doctor")
    @GetMapping("/by-personal-doctor/{doctorId}")
    public ResponseEntity<List<PatientDTO>> getPatientsByPersonalDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(patientService.getPatientsByPersonalDoctor(doctorId));
    }

    @Operation(summary = "Create patient")
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientDTO createdPatient = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    @Operation(summary = "Update patient")
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequestDTO patientRequestDTO
    ) {
        return ResponseEntity.ok(patientService.updatePatient(id, patientRequestDTO));
    }

    @Operation(summary = "Delete patient")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}