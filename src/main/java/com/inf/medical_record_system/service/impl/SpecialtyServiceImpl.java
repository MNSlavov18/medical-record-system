package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.Specialty;
import com.inf.medical_record_system.data.repo.SpecialtyRepository;
import com.inf.medical_record_system.dto.SpecialtyDTO;
import com.inf.medical_record_system.dto.SpecialtyRequestDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.SpecialtyService;
import com.inf.medical_record_system.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final MapperUtil mapperUtil;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, MapperUtil mapperUtil) {
        this.specialtyRepository = specialtyRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<SpecialtyDTO> getAllSpecialties() {
        return mapperUtil.mapList(specialtyRepository.findAll(), SpecialtyDTO.class);
    }

    @Override
    public SpecialtyDTO getSpecialtyById(Long id) {
        Specialty specialty = findSpecialtyById(id);
        return mapperUtil.map(specialty, SpecialtyDTO.class);
    }

    @Override
    public SpecialtyDTO createSpecialty(SpecialtyRequestDTO specialtyRequestDTO) {
        if (specialtyRepository.existsByName(specialtyRequestDTO.getName())) {
            throw new DuplicateResourceException("Specialty with this name already exists");
        }

        Specialty specialty = mapperUtil.map(specialtyRequestDTO, Specialty.class);
        Specialty savedSpecialty = specialtyRepository.save(specialty);

        return mapperUtil.map(savedSpecialty, SpecialtyDTO.class);
    }

    @Override
    public SpecialtyDTO updateSpecialty(Long id, SpecialtyRequestDTO specialtyRequestDTO) {
        Specialty specialty = findSpecialtyById(id);

        if (!specialty.getName().equals(specialtyRequestDTO.getName())
                && specialtyRepository.existsByName(specialtyRequestDTO.getName())) {
            throw new DuplicateResourceException("Specialty with this name already exists");
        }

        specialty.setName(specialtyRequestDTO.getName());

        Specialty updatedSpecialty = specialtyRepository.save(specialty);
        return mapperUtil.map(updatedSpecialty, SpecialtyDTO.class);
    }

    @Override
    public void deleteSpecialty(Long id) {
        Specialty specialty = findSpecialtyById(id);
        specialtyRepository.delete(specialty);
    }

    private Specialty findSpecialtyById(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
    }
}