package com.paycanvas.api.model.master;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record GradeRequest(@NotBlank String gradeName, @NotNull @DecimalMin("0.0") BigDecimal commissionRatePercent) {}
