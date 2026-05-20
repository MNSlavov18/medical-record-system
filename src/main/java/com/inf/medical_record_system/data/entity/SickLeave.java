package com.inf.medical_record_system.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "sick_leaves",
        indexes = {
                @Index(name = "idx_sick_leave_start_date", columnList = "start_date")
        }
)
public class SickLeave extends BaseEntity {

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private int numberOfDays;

    @OneToOne(optional = false)
    @JoinColumn(name = "examination_id", nullable = false, unique = true)
    private Examination examination;
}