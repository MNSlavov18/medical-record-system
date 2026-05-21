package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.SickLeaveDTO;
import com.inf.medical_record_system.dto.SickLeaveRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface SickLeaveService {

    List<SickLeaveDTO> getAllSickLeaves();

    SickLeaveDTO getSickLeaveById(Long id);

    SickLeaveDTO getSickLeaveByExamination(Long examinationId);

    List<SickLeaveDTO> getSickLeavesByPatient(Long patientId);

    List<SickLeaveDTO> getSickLeavesByDoctor(Long doctorId);

    List<SickLeaveDTO> getSickLeavesByPeriod(LocalDate startDate, LocalDate endDate);

    SickLeaveDTO createSickLeave(SickLeaveRequestDTO requestDTO);

    SickLeaveDTO updateSickLeave(Long id, SickLeaveRequestDTO requestDTO);

    void deleteSickLeave(Long id);
}