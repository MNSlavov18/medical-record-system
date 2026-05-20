package com.inf.medical_record_system.service.impl;

import com.inf.medical_record_system.data.entity.Role;
import com.inf.medical_record_system.data.entity.RoleName;
import com.inf.medical_record_system.data.entity.User;
import com.inf.medical_record_system.data.repo.RoleRepository;
import com.inf.medical_record_system.data.repo.UserRepository;
import com.inf.medical_record_system.dto.CreateUserDTO;
import com.inf.medical_record_system.dto.UpdateUserDTO;
import com.inf.medical_record_system.dto.UserDTO;
import com.inf.medical_record_system.exception.DuplicateResourceException;
import com.inf.medical_record_system.exception.InvalidOperationException;
import com.inf.medical_record_system.exception.ResourceNotFoundException;
import com.inf.medical_record_system.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserDTO)
                .toList();
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = findUserById(id);
        return mapToUserDTO(user);
    }

    @Override
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new DuplicateResourceException("User with this username already exists");
        }

        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new DuplicateResourceException("User with this email already exists");
        }

        Role role = findRoleByName(createUserDTO.getRoleName());

        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setEmail(createUserDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return mapToUserDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User user = findUserById(id);

        if (!user.getUsername().equals(updateUserDTO.getUsername())
                && userRepository.existsByUsername(updateUserDTO.getUsername())) {
            throw new DuplicateResourceException("User with this username already exists");
        }

        if (!user.getEmail().equals(updateUserDTO.getEmail())
                && userRepository.existsByEmail(updateUserDTO.getEmail())) {
            throw new DuplicateResourceException("User with this email already exists");
        }

        Role role = findRoleByName(updateUserDTO.getRoleName());

        user.setUsername(updateUserDTO.getUsername());
        user.setEmail(updateUserDTO.getEmail());
        user.setRole(role);

        User updatedUser = userRepository.save(user);

        return mapToUserDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);

        if (user.getDoctorProfile() != null || user.getPatientProfile() != null) {
            throw new InvalidOperationException("User cannot be deleted because it is connected to a doctor or patient profile");
        }

        userRepository.delete(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Role findRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoleName(user.getRole().getName());

        return userDTO;
    }
}