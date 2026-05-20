package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    Optional<Specialty> findByName(String name);

    boolean existsByName(String name);
}