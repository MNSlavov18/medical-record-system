package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.Doctor;
import com.inf.medical_record_system.data.entity.Patient;
import com.inf.medical_record_system.data.entity.RoleName;
import com.inf.medical_record_system.data.entity.User;
import com.inf.medical_record_system.data.repo.DoctorRepository;
import com.inf.medical_record_system.data.repo.ExaminationRepository;
import com.inf.medical_record_system.data.repo.HealthInsuranceStatusRepository;
import com.inf.medical_record_system.data.repo.PatientRepository;
import com.inf.medical_record_system.data.repo.UserRepository;
import com.inf.medical_record_system.dto.PatientDTO;
import com.inf.medical_record_system.dto.PatientRequestDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.CurrentUserService;
import com.inf.medical_record_system.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final ExaminationRepository examinationRepository;
    private final HealthInsuranceStatusRepository healthInsuranceStatusRepository;
    private final CurrentUserService currentUserService;

    public PatientServiceImpl(
            PatientRepository patientRepository,
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            ExaminationRepository examinationRepository,
            HealthInsuranceStatusRepository healthInsuranceStatusRepository,
            CurrentUserService currentUserService
    ) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.examinationRepository = examinationRepository;
        this.healthInsuranceStatusRepository = healthInsuranceStatusRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        if (currentUserService.isPatient()) {
            Patient currentPatient = findPatientById(currentUserService.getCurrentPatientId());
            return List.of(mapToPatientDTO(currentPatient));
        }

        return patientRepository.findAll()
                .stream()
                .map(this::mapToPatientDTO)
                .toList();
    }

    @Override
    public PatientDTO getPatientById(Long id) {
        Patient patient = findPatientById(id);
        validateCanReadPatient(patient);

        return mapToPatientDTO(patient);
    }

    @Override
    public PatientDTO getPatientByEgn(String egn) {
        Patient patient = patientRepository.findByEgn(egn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with EGN: " + egn));

        validateCanReadPatient(patient);

        return mapToPatientDTO(patient);
    }

    @Override
    public List<PatientDTO> getPatientsByPersonalDoctor(Long doctorId) {
        if (currentUserService.isPatient()) {
            Patient currentPatient = findPatientById(currentUserService.getCurrentPatientId());

            if (!currentPatient.getPersonalDoctor().getId().equals(doctorId)) {
                throw new InvalidOperationException("Patients can view only their own patient profile");
            }

            return List.of(mapToPatientDTO(currentPatient));
        }

        return patientRepository.findByPersonalDoctorId(doctorId)
                .stream()
                .map(this::mapToPatientDTO)
                .toList();
    }

    @Override
    public PatientDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEgn(patientRequestDTO.getEgn())) {
            throw new DuplicateResourceException("Patient with this EGN already exists");
        }

        User user = findUserById(patientRequestDTO.getUserId());
        validateUserCanBePatient(user, null);

        Doctor personalDoctor = findDoctorById(patientRequestDTO.getPersonalDoctorId());
        validateDoctorCanBePersonalDoctor(personalDoctor);

        Patient patient = new Patient();
        patient.setFullName(patientRequestDTO.getFullName());
        patient.setEgn(patientRequestDTO.getEgn());
        patient.setUser(user);
        patient.setPersonalDoctor(personalDoctor);

        Patient savedPatient = patientRepository.save(patient);

        return mapToPatientDTO(savedPatient);
    }

    @Override
    public PatientDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO) {
        Patient patient = findPatientById(id);

        if (!patient.getEgn().equals(patientRequestDTO.getEgn())
                && patientRepository.existsByEgn(patientRequestDTO.getEgn())) {
            throw new DuplicateResourceException("Patient with this EGN already exists");
        }

        User user = findUserById(patientRequestDTO.getUserId());
        validateUserCanBePatient(user, patient.getId());

        Doctor personalDoctor = findDoctorById(patientRequestDTO.getPersonalDoctorId());
        validateDoctorCanBePersonalDoctor(personalDoctor);

        patient.setFullName(patientRequestDTO.getFullName());
        patient.setEgn(patientRequestDTO.getEgn());
        patient.setUser(user);
        patient.setPersonalDoctor(personalDoctor);

        Patient updatedPatient = patientRepository.save(patient);

        return mapToPatientDTO(updatedPatient);
    }

    @Override
    public void deletePatient(Long id) {
        if (!currentUserService.isAdmin()) {
            throw new InvalidOperationException("Only administrators can delete patients");
        }

        Patient patient = findPatientById(id);

        if (!examinationRepository.findByPatientId(id).isEmpty()) {
            throw new InvalidOperationException("Patient cannot be deleted because there are examinations connected to this patient");
        }

        if (!healthInsuranceStatusRepository.findByPatientId(id).isEmpty()) {
            throw new InvalidOperationException("Patient cannot be deleted because there are health insurance records connected to this patient");
        }

        patientRepository.delete(patient);
    }

    private void validateCanReadPatient(Patient patient) {
        if (currentUserService.isAdmin() || currentUserService.isDoctor()) {
            return;
        }

        if (currentUserService.isPatient()) {
            Long currentPatientId = currentUserService.getCurrentPatientId();

            if (!patient.getId().equals(currentPatientId)) {
                throw new InvalidOperationException("Patients can view only their own patient profile");
            }

            return;
        }

        throw new InvalidOperationException("You do not have permission to view this patient");
    }

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    private void validateUserCanBePatient(User user, Long currentPatientId) {
        if (user.getRole().getName() != RoleName.PATIENT) {
            throw new InvalidOperationException("User must have role PATIENT to be connected to a patient profile");
        }

        patientRepository.findByUserId(user.getId())
                .ifPresent(existingPatient -> {
                    if (currentPatientId == null || !existingPatient.getId().equals(currentPatientId)) {
                        throw new InvalidOperationException("This user is already connected to another patient profile");
                    }
                });
    }

    private void validateDoctorCanBePersonalDoctor(Doctor doctor) {
        if (!doctor.isCanBePersonalDoctor()) {
            throw new InvalidOperationException("Selected doctor cannot be assigned as a personal doctor");
        }
    }

    private PatientDTO mapToPatientDTO(Patient patient) {
        PatientDTO patientDTO = new PatientDTO();

        patientDTO.setId(patient.getId());
        patientDTO.setFullName(patient.getFullName());
        patientDTO.setEgn(patient.getEgn());
        patientDTO.setUserId(patient.getUser().getId());
        patientDTO.setUsername(patient.getUser().getUsername());
        patientDTO.setPersonalDoctorId(patient.getPersonalDoctor().getId());
        patientDTO.setPersonalDoctorName(patient.getPersonalDoctor().getFullName());

        return patientDTO;
    }
}