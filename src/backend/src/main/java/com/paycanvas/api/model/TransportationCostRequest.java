package com.paycanvas.api.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

/**
 * 交通費登録・更新リクエスト。
 */
public class TransportationCostRequest {
  /**
   * 従業員ID。
   */
  @NotNull(message = "従業員IDは必須です")
  private Integer employeeId;

  /**
   * 月額交通費（円）。
   */
  @NotNull(message = "月額交通費は必須です")
  @PositiveOrZero(message = "月額交通費は0以上でなければなりません")
  private Integer monthlyAmount;

  /**
   * 通勤経路。
   */
  private String route;

  /**
   * 適用開始日。
   */
  @NotNull(message = "適用開始日は必須です")
  private LocalDate effectiveFrom;

  /**
   * 適用終了日。
   */
  private LocalDate effectiveTo;

  // Getters and Setters

  public Integer getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Integer employeeId) {
    this.employeeId = employeeId;
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
