package com.paycanvas.api.model.superadmin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyCreateRequest(
    @NotBlank String name,
    @NotBlank String postalCode,
    @NotBlank String address,
    @NotBlank String phone,
    @NotBlank String contactName,
    @NotBlank String contactKana,
    @Email @NotBlank String contactEmail) {}
