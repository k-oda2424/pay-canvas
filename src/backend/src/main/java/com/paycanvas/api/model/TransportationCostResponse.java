package com.paycanvas.api.model;

import java.time.LocalDate;

/**
 * 交通費レスポンス。
 */
public class TransportationCostResponse {
  private Integer id;
  private Integer employeeId;
  private String employeeName;
  private Integer monthlyAmount;
  private String route;
  private LocalDate effectiveFrom;
  private LocalDate effectiveTo;

  // Getters and Setters

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Integer employeeId) {
    this.employeeId = employeeId;
  }

  public String getEmployeeName() {
    return employeeName;
  }

  public void setEmployeeName(String employeeName) {
    this.employeeName = employeeName;
  }

  public Integer getMonthlyAmount() {
    return monthlyAmount;
  }

  public void setMonthlyAmount(Integer monthlyAmount) {
    this.monthlyAmount = monthlyAmount;
  }

  public String getRoute() {
    return route;
  }

  public void setRoute(String route) {
    this.route = route;
  }

  public LocalDate getEffectiveFrom() {
    return effectiveFrom;
  }

  public void setEffectiveFrom(LocalDate effectiveFrom) {
    this.effectiveFrom = effectiveFrom;
  }

  public LocalDate getEffectiveTo() {
    return effectiveTo;
  }

  public void setEffectiveTo(LocalDate effectiveTo) {
    this.effectiveTo = effectiveTo;
  }
}
