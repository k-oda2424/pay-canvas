package com.paycanvas.api.model.superadmin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminUserRequest(
    @NotNull Long companyId,
    @Email @NotBlank String email,
    @NotBlank String displayName,
    @Size(min = 8) String password) {}
