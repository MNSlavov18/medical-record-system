package com.inf.medical_record_system.service;

import com.inf.medical_record_system.data.entity.Doctor;
import com.inf.medical_record_system.data.entity.Patient;
import com.inf.medical_record_system.data.entity.RoleName;
import com.inf.medical_record_system.data.entity.User;
import com.inf.medical_record_system.data.repo.DoctorRepository;
import com.inf.medical_record_system.data.repo.PatientRepository;
import com.inf.medical_record_system.data.repo.UserRepository;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public CurrentUserService(
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public RoleName getCurrentUserRole() {
        return getCurrentUser().getRole().getName();
    }

    public boolean isAdmin() {
        return getCurrentUserRole() == RoleName.ADMIN;
    }

    public boolean isDoctor() {
        return getCurrentUserRole() == RoleName.DOCTOR;
    }

    public boolean isPatient() {
        return getCurrentUserRole() == RoleName.PATIENT;
    }

    public Long getCurrentDoctorId() {
        User currentUser = getCurrentUser();

        Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for current user"));

        return doctor.getId();
    }

    public Long getCurrentPatientId() {
        User currentUser = getCurrentUser();

        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for current user"));

        return patient.getId();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidOperationException("No authenticated user found");
        }

        if ("anonymousUser".equals(authentication.getPrincipal())) {
            throw new InvalidOperationException("No authenticated user found");
        }

        return authentication.getName();
    }
}