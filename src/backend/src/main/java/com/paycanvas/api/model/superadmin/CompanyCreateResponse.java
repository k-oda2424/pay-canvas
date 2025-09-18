package com.paycanvas.api.model.superadmin;

public record CompanyCreateResponse(
    Long id,
    String name,
    String status,
    String postalCode,
    String address,
    String phone,
    String contactName,
    String contactKana,
    String contactEmail) {}
