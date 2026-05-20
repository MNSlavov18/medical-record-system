package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    Optional<Diagnosis> findByCode(String code);

    Optional<Diagnosis> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);
}