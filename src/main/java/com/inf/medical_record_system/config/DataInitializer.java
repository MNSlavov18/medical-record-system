package com.inf.medical_record_system.config;

import com.inf.medical_record_system.data.entity.Role;
import com.inf.medical_record_system.data.entity.RoleName;
import com.inf.medical_record_system.data.repo.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        createRoleIfNotExists(RoleName.ADMIN);
        createRoleIfNotExists(RoleName.DOCTOR);
        createRoleIfNotExists(RoleName.PATIENT);
    }

    private void createRoleIfNotExists(RoleName roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}