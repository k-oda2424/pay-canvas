package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "m_employees")
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "grade_id")
  private EmployeeGrade grade;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "salary_tier_id")
  private SalaryTier salaryTier;

  @Column(name = "employment_type", nullable = false)
  private String employmentType;

  @Column(name = "store_name")
  private String storeName;

  @Column(name = "guaranteed_minimum_salary")
  private Integer guaranteedMinimumSalary;

  @Column(name = "manager_allowance")
  private Integer managerAllowance;

  @Column(name = "fixed_overtime_minutes")
  private Integer fixedOvertimeMinutes;

  @Column(name = "commission_reduction_rate")
  private BigDecimal commissionReductionRate;

  public Integer getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public String getName() {
    return name;
  }

  public EmployeeGrade getGrade() {
    return grade;
  }

  public SalaryTier getSalaryTier() {
    return salaryTier;
  }

  public String getEmploymentType() {
    return employmentType;
  }

  public String getStoreName() {
    return storeName;
  }

  public Integer getGuaranteedMinimumSalary() {
    return guaranteedMinimumSalary;
  }

  public Integer getManagerAllowance() {
    return managerAllowance;
  }

  public Integer getFixedOvertimeMinutes() {
    return fixedOvertimeMinutes;
  }

  public BigDecimal getCommissionReductionRate() {
    return commissionReductionRate;
  }
}
