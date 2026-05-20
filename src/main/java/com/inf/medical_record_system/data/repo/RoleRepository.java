package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.Role;
import com.inf.medical_record_system.data.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}