package com.inf.medical_record_system.web.api;

import com.inf.medical_record_system.dto.DoctorDTO;
import com.inf.medical_record_system.dto.DoctorRequestDTO;
import com.inf.medical_record_system.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Doctors", description = "Operations for managing doctors")
@RestController
@RequestMapping("/api/doctors")
public class DoctorApiController {

    private final DoctorService doctorService;

    public DoctorApiController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Operation(summary = "Find all doctors")
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @Operation(summary = "Find doctor by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @Operation(summary = "Find doctors that can be personal doctors")
    @GetMapping("/personal-doctors")
    public ResponseEntity<List<DoctorDTO>> getPersonalDoctors() {
        return ResponseEntity.ok(doctorService.getPersonalDoctors());
    }

    @Operation(summary = "Find doctors by specialty")
    @GetMapping("/by-specialty/{specialtyId}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialty(@PathVariable Long specialtyId) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialty(specialtyId));
    }

    @Operation(summary = "Search doctors by name")
    @GetMapping("/search")
    public ResponseEntity<List<DoctorDTO>> searchDoctorsByName(@RequestParam String name) {
        return ResponseEntity.ok(doctorService.searchDoctorsByName(name));
    }

    @Operation(summary = "Create doctor")
    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody DoctorRequestDTO doctorRequestDTO) {
        DoctorDTO createdDoctor = doctorService.createDoctor(doctorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDoctor);
    }

    @Operation(summary = "Update doctor")
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequestDTO doctorRequestDTO
    ) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctorRequestDTO));
    }

    @Operation(summary = "Delete doctor")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}