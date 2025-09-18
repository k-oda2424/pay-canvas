package com.paycanvas.api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record EmployeeMasterRequest(
    @NotBlank String name,
    @NotBlank String employmentType,
    Integer gradeId,
    Integer salaryTierId,
    @Min(1) Integer storeId,
    @Min(0) Integer guaranteedMinimumSalary,
    @Min(0) Integer managerAllowance) {}
