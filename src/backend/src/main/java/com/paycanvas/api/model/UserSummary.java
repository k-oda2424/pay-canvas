package com.paycanvas.api.model;

import java.util.List;

public record UserSummary(
    long userId,
    long companyId,
    String companyName,
    String role,
    List<String> enabledFeatures,
    String name) {}
