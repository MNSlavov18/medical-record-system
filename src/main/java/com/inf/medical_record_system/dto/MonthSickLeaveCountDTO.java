package com.inf.medical_record_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthSickLeaveCountDTO {

    private Integer year;

    private Integer month;

    private Long sickLeaveCount;
}