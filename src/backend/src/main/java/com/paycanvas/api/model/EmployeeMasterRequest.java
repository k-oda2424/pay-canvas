package com.paycanvas.api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public record EmployeeMasterRequest(
    @Min(1) Integer displayId,
    @NotBlank String name,
    @NotBlank String employmentType,
    Integer gradeId,
    @Min(1) Integer storeId,
    Integer workPatternId,
    Boolean isBoardMember,
    @Min(0) Integer boardCompensation,
    @Min(0) Integer guaranteedMinimumSalary,
    @Min(0) Integer managerAllowance,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDate) {}
