package com.inf.medical_record_system.web.view.controller;

import com.inf.medical_record_system.data.entity.Doctor;
import com.inf.medical_record_system.data.entity.Patient;
import com.inf.medical_record_system.data.entity.Role;
import com.inf.medical_record_system.data.entity.User;
import com.inf.medical_record_system.data.repo.DoctorRepository;
import com.inf.medical_record_system.data.repo.PatientRepository;
import com.inf.medical_record_system.data.repo.RoleRepository;
import com.inf.medical_record_system.data.repo.UserRepository;
import com.inf.medical_record_system.dto.AdminCreateUserDTO;
import com.inf.medical_record_system.dto.AdminEditUserDTO;
import com.inf.medical_record_system.dto.AdminUserViewDTO;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserViewController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public UserViewController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<AdminUserViewDTO> users = userRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .map(this::mapToAdminUserViewDTO)
                .toList();

        model.addAttribute("users", users);

        return "users/list-users";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("adminCreateUserDTO", new AdminCreateUserDTO());
        model.addAttribute("roles", roleRepository.findAll());

        return "users/add-user";
    }

    @PostMapping("/add")
    public String addUser(
            @Valid AdminCreateUserDTO adminCreateUserDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "users/add-user";
        }

        if (userRepository.findByUsername(adminCreateUserDTO.getUsername()).isPresent()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("errorMessage", "Username already exists");
            return "users/add-user";
        }

        if (userRepository.findByEmail(adminCreateUserDTO.getEmail()).isPresent()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("errorMessage", "Email already exists");
            return "users/add-user";
        }

        Role role = roleRepository.findById(adminCreateUserDTO.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Selected role does not exist"));

        User user = new User();
        user.setUsername(adminCreateUserDTO.getUsername());
        user.setEmail(adminCreateUserDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(adminCreateUserDTO.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        return "redirect:/users?created";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(
            @PathVariable Long id,
            Model model
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        AdminEditUserDTO adminEditUserDTO = new AdminEditUserDTO();
        adminEditUserDTO.setUsername(user.getUsername());
        adminEditUserDTO.setEmail(user.getEmail());
        adminEditUserDTO.setRoleId(user.getRole().getId());

        model.addAttribute("userId", user.getId());
        model.addAttribute("adminEditUserDTO", adminEditUserDTO);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("assignedInfo", getAssignedInfo(user.getId()));

        return "users/edit-user";
    }

    @PostMapping("/edit/{id}")
    public String editUser(
            @PathVariable Long id,
            @Valid AdminEditUserDTO adminEditUserDTO,
            BindingResult bindingResult,
            Model model
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (adminEditUserDTO.getPassword() != null
                && !adminEditUserDTO.getPassword().isBlank()
                && adminEditUserDTO.getPassword().length() < 4) {
            bindingResult.rejectValue(
                    "password",
                    "password.tooShort",
                    "Password must be at least 4 characters"
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("assignedInfo", getAssignedInfo(id));
            return "users/edit-user";
        }

        Optional<User> existingUsername = userRepository.findByUsername(adminEditUserDTO.getUsername());
        if (existingUsername.isPresent() && !existingUsername.get().getId().equals(id)) {
            model.addAttribute("userId", id);
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("assignedInfo", getAssignedInfo(id));
            model.addAttribute("errorMessage", "Username already exists");
            return "users/edit-user";
        }

        Optional<User> existingEmail = userRepository.findByEmail(adminEditUserDTO.getEmail());
        if (existingEmail.isPresent() && !existingEmail.get().getId().equals(id)) {
            model.addAttribute("userId", id);
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("assignedInfo", getAssignedInfo(id));
            model.addAttribute("errorMessage", "Email already exists");
            return "users/edit-user";
        }

        Role role = roleRepository.findById(adminEditUserDTO.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Selected role does not exist"));

        user.setUsername(adminEditUserDTO.getUsername());
        user.setEmail(adminEditUserDTO.getEmail());
        user.setRole(role);

        if (adminEditUserDTO.getPassword() != null && !adminEditUserDTO.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(adminEditUserDTO.getPassword()));
        }

        userRepository.save(user);

        return "redirect:/users?updated";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAssignedToDoctor = doctorRepository.findByUserId(id).isPresent();
        boolean isAssignedToPatient = patientRepository.findByUserId(id).isPresent();

        if (isAssignedToDoctor || isAssignedToPatient) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This user is assigned to a doctor or patient and cannot be deleted."
            );
            return "redirect:/users";
        }

        if ("admin".equalsIgnoreCase(user.getUsername())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Default admin user cannot be deleted."
            );
            return "redirect:/users";
        }

        userRepository.delete(user);

        return "redirect:/users?deleted";
    }

    private AdminUserViewDTO mapToAdminUserViewDTO(User user) {
        AdminUserViewDTO dto = new AdminUserViewDTO()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setRoleName(user.getRole().getName().name());

        Optional<Doctor> doctor = doctorRepository.findByUserId(user.getId());

        if (doctor.isPresent()) {
            return dto
                    .setAssigned(true)
                    .setAssignedType("Doctor")
                    .setAssignedEntityId(doctor.get().getId())
                    .setAssignedName(doctor.get().getFullName());
        }

        Optional<Patient> patient = patientRepository.findByUserId(user.getId());

        if (patient.isPresent()) {
            return dto
                    .setAssigned(true)
                    .setAssignedType("Patient")
                    .setAssignedEntityId(patient.get().getId())
                    .setAssignedName(patient.get().getFullName());
        }

        return dto
                .setAssigned(false)
                .setAssignedType("Not assigned")
                .setAssignedEntityId(null)
                .setAssignedName("-");
    }

    private String getAssignedInfo(Long userId) {
        Optional<Doctor> doctor = doctorRepository.findByUserId(userId);

        if (doctor.isPresent()) {
            return "Doctor: " + doctor.get().getFullName();
        }

        Optional<Patient> patient = patientRepository.findByUserId(userId);

        if (patient.isPresent()) {
            return "Patient: " + patient.get().getFullName();
        }

        return "Not assigned";
    }
}