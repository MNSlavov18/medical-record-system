package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.SpecialtyDTO;

import java.util.List;

public interface SpecialtyService {

    List<SpecialtyDTO> getAllSpecialties();

    SpecialtyDTO getSpecialtyById(Long id);

    SpecialtyDTO createSpecialty(SpecialtyDTO specialtyDTO);

    SpecialtyDTO updateSpecialty(Long id, SpecialtyDTO specialtyDTO);

    void deleteSpecialty(Long id);
}