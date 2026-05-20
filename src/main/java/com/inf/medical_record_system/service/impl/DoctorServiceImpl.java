package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.Doctor;
import com.inf.medical_record_system.data.entity.RoleName;
import com.inf.medical_record_system.data.entity.Specialty;
import com.inf.medical_record_system.data.entity.User;
import com.inf.medical_record_system.data.repo.DoctorRepository;
import com.inf.medical_record_system.data.repo.SpecialtyRepository;
import com.inf.medical_record_system.data.repo.UserRepository;
import com.inf.medical_record_system.dto.DoctorDTO;
import com.inf.medical_record_system.dto.DoctorRequestDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorServiceImpl(
            DoctorRepository doctorRepository,
            UserRepository userRepository,
            SpecialtyRepository specialtyRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::mapToDoctorDTO)
                .toList();
    }

    @Override
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = findDoctorById(id);
        return mapToDoctorDTO(doctor);
    }

    @Override
    public List<DoctorDTO> getPersonalDoctors() {
        return doctorRepository.findByCanBePersonalDoctorTrue()
                .stream()
                .map(this::mapToDoctorDTO)
                .toList();
    }

    @Override
    public List<DoctorDTO> getDoctorsBySpecialty(Long specialtyId) {
        return doctorRepository.findBySpecialtyId(specialtyId)
                .stream()
                .map(this::mapToDoctorDTO)
                .toList();
    }

    @Override
    public List<DoctorDTO> searchDoctorsByName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidOperationException("Doctor name search cannot be empty");
        }

        return doctorRepository.findByFullNameContainingIgnoreCase(name.trim())
                .stream()
                .map(this::mapToDoctorDTO)
                .toList();
    }

    @Override
    public DoctorDTO createDoctor(DoctorRequestDTO doctorRequestDTO) {
        if (doctorRepository.existsByUniqueIdentificationNumber(doctorRequestDTO.getUniqueIdentificationNumber())) {
            throw new DuplicateResourceException("Doctor with this unique identification number already exists");
        }

        User user = findUserById(doctorRequestDTO.getUserId());
        validateUserCanBeDoctor(user, null);

        Specialty specialty = findSpecialtyById(doctorRequestDTO.getSpecialtyId());

        Doctor doctor = new Doctor();
        doctor.setUniqueIdentificationNumber(doctorRequestDTO.getUniqueIdentificationNumber());
        doctor.setFullName(doctorRequestDTO.getFullName());
        doctor.setUser(user);
        doctor.setSpecialty(specialty);
        doctor.setCanBePersonalDoctor(doctorRequestDTO.isCanBePersonalDoctor());

        Doctor savedDoctor = doctorRepository.save(doctor);

        return mapToDoctorDTO(savedDoctor);
    }

    @Override
    public DoctorDTO updateDoctor(Long id, DoctorRequestDTO doctorRequestDTO) {
        Doctor doctor = findDoctorById(id);

        if (!doctor.getUniqueIdentificationNumber().equals(doctorRequestDTO.getUniqueIdentificationNumber())
                && doctorRepository.existsByUniqueIdentificationNumber(doctorRequestDTO.getUniqueIdentificationNumber())) {
            throw new DuplicateResourceException("Doctor with this unique identification number already exists");
        }

        User user = findUserById(doctorRequestDTO.getUserId());
        validateUserCanBeDoctor(user, doctor.getId());

        Specialty specialty = findSpecialtyById(doctorRequestDTO.getSpecialtyId());

        doctor.setUniqueIdentificationNumber(doctorRequestDTO.getUniqueIdentificationNumber());
        doctor.setFullName(doctorRequestDTO.getFullName());
        doctor.setUser(user);
        doctor.setSpecialty(specialty);
        doctor.setCanBePersonalDoctor(doctorRequestDTO.isCanBePersonalDoctor());

        Doctor updatedDoctor = doctorRepository.save(doctor);

        return mapToDoctorDTO(updatedDoctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = findDoctorById(id);

        if (!doctor.getPersonalPatients().isEmpty()) {
            throw new InvalidOperationException("Doctor cannot be deleted because there are patients registered with this doctor");
        }

        if (!doctor.getExaminations().isEmpty()) {
            throw new InvalidOperationException("Doctor cannot be deleted because there are examinations connected to this doctor");
        }

        doctorRepository.delete(doctor);
    }

    private Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Specialty findSpecialtyById(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));
    }

    private void validateUserCanBeDoctor(User user, Long currentDoctorId) {
        if (user.getRole().getName() != RoleName.DOCTOR) {
            throw new InvalidOperationException("User must have role DOCTOR to be connected to a doctor profile");
        }

        doctorRepository.findByUserId(user.getId())
                .ifPresent(existingDoctor -> {
                    if (currentDoctorId == null || !existingDoctor.getId().equals(currentDoctorId)) {
                        throw new InvalidOperationException("This user is already connected to another doctor profile");
                    }
                });
    }

    private DoctorDTO mapToDoctorDTO(Doctor doctor) {
        DoctorDTO doctorDTO = new DoctorDTO();

        doctorDTO.setId(doctor.getId());
        doctorDTO.setUniqueIdentificationNumber(doctor.getUniqueIdentificationNumber());
        doctorDTO.setFullName(doctor.getFullName());
        doctorDTO.setUserId(doctor.getUser().getId());
        doctorDTO.setUsername(doctor.getUser().getUsername());
        doctorDTO.setSpecialtyId(doctor.getSpecialty().getId());
        doctorDTO.setSpecialtyName(doctor.getSpecialty().getName());
        doctorDTO.setCanBePersonalDoctor(doctor.isCanBePersonalDoctor());

        return doctorDTO;
    }
}