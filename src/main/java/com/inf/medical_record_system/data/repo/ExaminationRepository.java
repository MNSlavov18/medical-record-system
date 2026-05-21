package com.inf.medical_record_system.data.repo;

import com.inf.medical_record_system.data.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inf.medical_record_system.data.entity.PaymentSource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByPatientId(Long patientId);

    List<Examination> findByDoctorId(Long doctorId);

    List<Examination> findByDiagnosisId(Long diagnosisId);

    List<Examination> findByDoctorIdAndExaminationDateBetween(
            Long doctorId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Examination> findByExaminationDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("""
        SELECT e.diagnosis.id, e.diagnosis.name, COUNT(e)
        FROM Examination e
        GROUP BY e.diagnosis.id, e.diagnosis.name
        ORDER BY COUNT(e) DESC
        """)
    List<Object[]> countExaminationsByDiagnosis();

    @Query("""
        SELECT SUM(e.price)
        FROM Examination e
        WHERE e.paymentSource = :paymentSource
        """)
    BigDecimal calculateTotalValueByPaymentSource(@Param("paymentSource") PaymentSource paymentSource);

    @Query("""
        SELECT SUM(e.price)
        FROM Examination e
        WHERE e.paymentSource = :paymentSource
        AND e.doctor.id = :doctorId
        """)
    BigDecimal calculateTotalValueByPaymentSourceAndDoctor(
            @Param("paymentSource") PaymentSource paymentSource,
            @Param("doctorId") Long doctorId
    );

    @Query("""
        SELECT e.doctor.id, e.doctor.fullName, COUNT(e)
        FROM Examination e
        GROUP BY e.doctor.id, e.doctor.fullName
        ORDER BY COUNT(e) DESC
        """)
    List<Object[]> countVisitsByDoctor();
}