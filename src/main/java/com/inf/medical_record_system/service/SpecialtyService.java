package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.SpecialtyDTO;
import com.inf.medical_record_system.dto.SpecialtyRequestDTO;

import java.util.List;

public interface SpecialtyService {

    List<SpecialtyDTO> getAllSpecialties();

    SpecialtyDTO getSpecialtyById(Long id);

    SpecialtyDTO createSpecialty(SpecialtyRequestDTO specialtyRequestDTO);

    SpecialtyDTO updateSpecialty(Long id, SpecialtyRequestDTO specialtyRequestDTO);

    void deleteSpecialty(Long id);
}