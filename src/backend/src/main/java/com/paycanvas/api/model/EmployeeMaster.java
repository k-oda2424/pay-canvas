package com.paycanvas.api.model;

public record EmployeeMaster(
    int id,
    Integer displayId,
    String name,
    Integer gradeId,
    String grade,
    String employmentType,
    Integer workPatternId,
    String workPatternName,
    Integer storeId,
    String storeName,
    Integer guaranteedMinimumSalary,
    Integer managerAllowance,
    Boolean isBoardMember,
    Integer boardCompensation,
    java.time.LocalDate hireDate) {}
