package com.paycanvas.api.model.master;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SalaryTierRequest(
    @NotBlank String planName,
    @NotNull @Min(0) Integer monthlyDaysOff,
    @NotNull @Min(0) Integer baseSalary) {}
