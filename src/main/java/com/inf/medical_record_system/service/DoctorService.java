package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.DoctorDTO;
import com.inf.medical_record_system.dto.DoctorRequestDTO;

import java.util.List;

public interface DoctorService {

    List<DoctorDTO> getAllDoctors();

    DoctorDTO getDoctorById(Long id);

    List<DoctorDTO> getPersonalDoctors();

    List<DoctorDTO> getDoctorsBySpecialty(Long specialtyId);

    List<DoctorDTO> searchDoctorsByName(String name);

    DoctorDTO createDoctor(DoctorRequestDTO doctorRequestDTO);

    DoctorDTO updateDoctor(Long id, DoctorRequestDTO doctorRequestDTO);

    void deleteDoctor(Long id);
}