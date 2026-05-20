package com.inf.medical_record_system.web.api;

import com.inf.medical_record_system.dto.SpecialtyDTO;
import com.inf.medical_record_system.dto.SpecialtyRequestDTO;
import com.inf.medical_record_system.service.SpecialtyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Specialties", description = "Operations for managing medical specialties")
@RestController
@RequestMapping("/api/specialties")
public class SpecialtyApiController {

    private final SpecialtyService specialtyService;

    public SpecialtyApiController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @Operation(summary = "Find all specialties")
    @GetMapping
    public ResponseEntity<List<SpecialtyDTO>> getAllSpecialties() {
        return ResponseEntity.ok(specialtyService.getAllSpecialties());
    }

    @Operation(summary = "Find specialty by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyDTO> getSpecialtyById(@PathVariable Long id) {
        return ResponseEntity.ok(specialtyService.getSpecialtyById(id));
    }

    @Operation(summary = "Create specialty")
    @PostMapping
    public ResponseEntity<SpecialtyDTO> createSpecialty(@Valid @RequestBody SpecialtyRequestDTO specialtyRequestDTO) {
        SpecialtyDTO createdSpecialty = specialtyService.createSpecialty(specialtyRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSpecialty);
    }

    @Operation(summary = "Update specialty")
    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyDTO> updateSpecialty(
            @PathVariable Long id,
            @Valid @RequestBody SpecialtyRequestDTO specialtyRequestDTO
    ) {
        return ResponseEntity.ok(specialtyService.updateSpecialty(id, specialtyRequestDTO));
    }

    @Operation(summary = "Delete specialty")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }
}