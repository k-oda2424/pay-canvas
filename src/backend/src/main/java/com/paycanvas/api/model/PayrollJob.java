package com.paycanvas.api.model;

public record PayrollJob(String id, String targetMonth, String status, int progress, String startedAt) {}
