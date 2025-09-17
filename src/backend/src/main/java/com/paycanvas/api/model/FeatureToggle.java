package com.paycanvas.api.model;

public record FeatureToggle(String id, String name, String description, int enabledTenants, boolean isEnabled) {}
