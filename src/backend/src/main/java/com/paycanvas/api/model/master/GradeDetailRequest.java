package com.paycanvas.api.model.master;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record GradeDetailRequest(
    @NotNull Integer gradeId,
    Integer workPatternId,
    @NotBlank String employmentType,
    Integer baseSalary,
    Integer baseHourlyWage,
    Integer lateStartDeduction,
    Integer saturdayBonus,
    Integer sundayHolidayBonus,
    @DecimalMin("0.0") BigDecimal commissionRate,
    @DecimalMin("0.0") BigDecimal personalBonusRate,
    @DecimalMin("0.0") BigDecimal storeBonusRate,
    Integer minProductivity) {}
