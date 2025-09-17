package com.paycanvas.api.model.master;

import jakarta.validation.constraints.NotBlank;

public record StoreRequest(@NotBlank String name, String storeType, String address) {}
