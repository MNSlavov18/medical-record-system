package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.TreatmentDTO;
import com.inf.medical_record_system.dto.TreatmentRequestDTO;

import java.util.List;

public interface TreatmentService {

    List<TreatmentDTO> getAllTreatments();

    TreatmentDTO getTreatmentById(Long id);

    TreatmentDTO getTreatmentByExamination(Long examinationId);

    TreatmentDTO createTreatment(TreatmentRequestDTO requestDTO);

    TreatmentDTO updateTreatment(Long id, TreatmentRequestDTO requestDTO);

    void deleteTreatment(Long id);
}