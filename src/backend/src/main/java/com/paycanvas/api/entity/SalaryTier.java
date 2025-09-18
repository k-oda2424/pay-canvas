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

/**
 * 給与階層マスターエンティティクラス。
 *
 * <p>このクラスは企業ごとの給与階層情報を管理するエンティティです。
 * データベースの「m_salary_tiers」テーブルにマッピングされ、
 * 従業員の給与プラン、基本給、休日数などの情報を格納します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>給与プランの管理（正社員、アルバイトなど）</li>
 *   <li>基本給の設定と管理</li>
 *   <li>月間休日数の設定</li>
 *   <li>給与計算時の基本給算出</li>
 * </ul>
 */
@Entity
@Table(name = "m_salary_tiers")
public class SalaryTier {
  /**
   * 給与階層ID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 所属企業。
   * この給与階層が設定されている企業への参照です。
   * 企業ごとに独自の給与体系を持つことができます。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  /**
   * プラン名。
   * 給与プランの名称です（例：「正社員プラン」「アルバイトプラン」など）。
   */
  @Column(name = "plan_name", nullable = false)
  private String planName;

  /**
   * 月間休日数。
   * この給与階層における1ヶ月あたりの休日数です。
   * 給与計算時の勤務日数算出に使用されます。
   */
  @Column(name = "monthly_days_off", nullable = false)
  private int monthlyDaysOff;

  /**
   * 基本給。
   * この給与階層の基本給額（円）です。
   * 歩合給や手当などと組み合わせて総給与を算出します。
   */
  @Column(name = "base_salary", nullable = false)
  private int baseSalary;

  public Integer getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getPlanName() {
    return planName;
  }

  public void setPlanName(String planName) {
    this.planName = planName;
  }

  public int getMonthlyDaysOff() {
    return monthlyDaysOff;
  }

  public void setMonthlyDaysOff(int monthlyDaysOff) {
    this.monthlyDaysOff = monthlyDaysOff;
  }

  public int getBaseSalary() {
    return baseSalary;
  }

  public void setBaseSalary(int baseSalary) {
    this.baseSalary = baseSalary;
  }
}
