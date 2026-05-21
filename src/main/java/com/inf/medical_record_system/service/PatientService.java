package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.PatientDTO;
import com.inf.medical_record_system.dto.PatientRequestDTO;

import java.util.List;

public interface PatientService {

    List<PatientDTO> getAllPatients();

    PatientDTO getPatientById(Long id);

    PatientDTO getPatientByEgn(String egn);

    List<PatientDTO> getPatientsByPersonalDoctor(Long doctorId);

    PatientDTO createPatient(PatientRequestDTO patientRequestDTO);

    PatientDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO);

    void deletePatient(Long id);
}