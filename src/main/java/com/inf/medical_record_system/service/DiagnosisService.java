package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.DiagnosisDTO;
import com.inf.medical_record_system.dto.DiagnosisRequestDTO;

import java.util.List;

public interface DiagnosisService {

    List<DiagnosisDTO> getAllDiagnoses();

    DiagnosisDTO getDiagnosisById(Long id);

    DiagnosisDTO createDiagnosis(DiagnosisRequestDTO diagnosisRequestDTO);

    DiagnosisDTO updateDiagnosis(Long id, DiagnosisRequestDTO diagnosisRequestDTO);

    void deleteDiagnosis(Long id);
}