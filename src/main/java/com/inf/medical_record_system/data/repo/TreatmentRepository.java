package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    Optional<Treatment> findByExaminationId(Long examinationId);
}