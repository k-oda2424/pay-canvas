package com.paycanvas.api.model;

public record EmployeeMaster(
    int id,
    String name,
    Integer gradeId,
    String grade,
    String employmentType,
    Integer salaryTierId,
    String salaryPlan,
    Integer storeId,
    String storeName,
    Integer guaranteedMinimumSalary,
    Integer managerAllowance) {}
