package com.inf.medical_record_system.config;

import com.inf.medical_record_system.data.entity.Role;
import com.inf.medical_record_system.data.entity.RoleName;
import com.inf.medical_record_system.data.entity.User;
import com.inf.medical_record_system.data.repo.RoleRepository;
import com.inf.medical_record_system.data.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultAdminSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createRoleIfMissing(RoleName.ADMIN);
        createRoleIfMissing(RoleName.DOCTOR);
        createRoleIfMissing(RoleName.PATIENT);

        boolean adminExists = userRepository.findByUsername("admin").isPresent();

        if (!adminExists) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ADMIN role was not created"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@test.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);

            userRepository.save(admin);

            System.out.println("Default admin created:");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
        }
    }

    private void createRoleIfMissing(RoleName roleName) {
        boolean roleExists = roleRepository.findByName(roleName).isPresent();

        if (!roleExists) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}