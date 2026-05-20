package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.DiagnosisDTO;

import java.util.List;

public interface DiagnosisService {

    List<DiagnosisDTO> getAllDiagnoses();

    DiagnosisDTO getDiagnosisById(Long id);

    DiagnosisDTO createDiagnosis(DiagnosisDTO diagnosisDTO);

    DiagnosisDTO updateDiagnosis(Long id, DiagnosisDTO diagnosisDTO);

    void deleteDiagnosis(Long id);
}