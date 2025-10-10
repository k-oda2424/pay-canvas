package com.paycanvas.api.model.master;

import java.math.BigDecimal;

public record GradeDetailResponse(
    Integer id,
    Integer gradeId,
    String gradeName,
    Integer workPatternId,
    String workPatternName,
    String employmentType,
    Integer baseSalary,
    Integer baseHourlyWage,
    Integer lateStartDeduction,
    Integer saturdayBonus,
    Integer sundayHolidayBonus,
    BigDecimal commissionRate,
    BigDecimal personalBonusRate,
    BigDecimal storeBonusRate,
    Integer minProductivity) {}
