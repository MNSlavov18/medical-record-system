package com.inf.medical_record_system.service;

import com.inf.medical_record_system.dto.CreateUserDTO;
import com.inf.medical_record_system.dto.UpdateUserDTO;
import com.inf.medical_record_system.dto.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO createUser(CreateUserDTO createUserDTO);

    UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO);

    void deleteUser(Long id);
}