package com.paycanvas.api.model;

public record Payslip(
    String id,
    String employeeName,
    String role,
    int baseSalary,
    int allowances,
    int deductions,
    int netPay,
    String status) {}
