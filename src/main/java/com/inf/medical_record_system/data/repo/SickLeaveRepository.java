package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    Optional<SickLeave> findByExaminationId(Long examinationId);

    List<SickLeave> findByExaminationPatientId(Long patientId);

    List<SickLeave> findByExaminationDoctorId(Long doctorId);

    List<SickLeave> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("""
        SELECT YEAR(s.startDate), MONTH(s.startDate), COUNT(s)
        FROM SickLeave s
        GROUP BY YEAR(s.startDate), MONTH(s.startDate)
        ORDER BY COUNT(s) DESC
        """)
    List<Object[]> countSickLeavesByMonth();

    @Query("""
        SELECT s.examination.doctor.id, s.examination.doctor.fullName, COUNT(s)
        FROM SickLeave s
        GROUP BY s.examination.doctor.id, s.examination.doctor.fullName
        ORDER BY COUNT(s) DESC
        """)
    List<Object[]> countSickLeavesByDoctor();
}