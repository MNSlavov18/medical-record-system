package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.ExaminationDTO;
import com.inf.medical_record_system.dto.ExaminationRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface ExaminationService {

    List<ExaminationDTO> getAllExaminations();

    ExaminationDTO getExaminationById(Long id);

    List<ExaminationDTO> getExaminationsByPatient(Long patientId);

    List<ExaminationDTO> getExaminationsByDoctor(Long doctorId);

    List<ExaminationDTO> getExaminationsByDiagnosis(Long diagnosisId);

    List<ExaminationDTO> getExaminationsByPeriod(LocalDate startDate, LocalDate endDate);

    List<ExaminationDTO> getExaminationsByDoctorAndPeriod(Long doctorId, LocalDate startDate, LocalDate endDate);

    ExaminationDTO createExamination(ExaminationRequestDTO requestDTO);

    ExaminationDTO updateExamination(Long id, ExaminationRequestDTO requestDTO);

    void deleteExamination(Long id);
}