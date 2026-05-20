package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.Diagnosis;
import com.inf.medical_record_system.data.repo.DiagnosisRepository;
import com.inf.medical_record_system.dto.DiagnosisDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.DiagnosisService;
import com.inf.medical_record_system.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final MapperUtil mapperUtil;

    public DiagnosisServiceImpl(DiagnosisRepository diagnosisRepository, MapperUtil mapperUtil) {
        this.diagnosisRepository = diagnosisRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<DiagnosisDTO> getAllDiagnoses() {
        return mapperUtil.mapList(diagnosisRepository.findAll(), DiagnosisDTO.class);
    }

    @Override
    public DiagnosisDTO getDiagnosisById(Long id) {
        Diagnosis diagnosis = findDiagnosisById(id);
        return mapperUtil.map(diagnosis, DiagnosisDTO.class);
    }

    @Override
    public DiagnosisDTO createDiagnosis(DiagnosisDTO diagnosisDTO) {
        if (diagnosisRepository.existsByCode(diagnosisDTO.getCode())) {
            throw new DuplicateResourceException("Diagnosis with this code already exists");
        }

        if (diagnosisRepository.existsByName(diagnosisDTO.getName())) {
            throw new DuplicateResourceException("Diagnosis with this name already exists");
        }

        Diagnosis diagnosis = mapperUtil.map(diagnosisDTO, Diagnosis.class);
        Diagnosis savedDiagnosis = diagnosisRepository.save(diagnosis);

        return mapperUtil.map(savedDiagnosis, DiagnosisDTO.class);
    }

    @Override
    public DiagnosisDTO updateDiagnosis(Long id, DiagnosisDTO diagnosisDTO) {
        Diagnosis diagnosis = findDiagnosisById(id);

        if (!diagnosis.getCode().equals(diagnosisDTO.getCode())
                && diagnosisRepository.existsByCode(diagnosisDTO.getCode())) {
            throw new DuplicateResourceException("Diagnosis with this code already exists");
        }

        if (!diagnosis.getName().equals(diagnosisDTO.getName())
                && diagnosisRepository.existsByName(diagnosisDTO.getName())) {
            throw new DuplicateResourceException("Diagnosis with this name already exists");
        }

        diagnosis.setCode(diagnosisDTO.getCode());
        diagnosis.setName(diagnosisDTO.getName());
        diagnosis.setDescription(diagnosisDTO.getDescription());

        Diagnosis updatedDiagnosis = diagnosisRepository.save(diagnosis);
        return mapperUtil.map(updatedDiagnosis, DiagnosisDTO.class);
    }

    @Override
    public void deleteDiagnosis(Long id) {
        Diagnosis diagnosis = findDiagnosisById(id);
        diagnosisRepository.delete(diagnosis);
    }

    private Diagnosis findDiagnosisById(Long id) {
        return diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with id: " + id));
    }
}