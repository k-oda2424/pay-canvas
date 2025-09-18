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

/**
 * 従業員マスターエンティティクラス。
 *
 * <p>このクラスは企業に所属する従業員の情報を管理するエンティティです。
 * データベースの「m_employees」テーブルにマッピングされ、従業員の基本情報および給与関連情報を格納します。</p>
 *
 * <p>主な機能：</p>
 * <ul>
 *   <li>従業員の基本情報管理（氏名、雇用形態など）</li>
 *   <li>等級・給与階層との関連付け</li>
 *   <li>給与計算に必要な各種設定値の管理</li>
 *   <li>所属企業・店舗との関連付け</li>
 * </ul>
 */
@Entity
@Table(name = "m_employees")
public class Employee {
  /**
   * 従業員ID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 所属企業。
   * この従業員が所属する企業への参照です。
   * 遅延読み込み（LAZY）で設定されています。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  /**
   * 従業員氏名。
   * 従業員の氏名（フルネーム）を格納します。
   */
  @Column(nullable = false)
  private String name;

  /**
   * 従業員等級。
   * 従業員の職位・等級への参照です。
   * 給与計算や権限管理に使用されます。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "grade_id")
  private EmployeeGrade grade;

  /**
   * 給与階層。
   * 従業員の給与階層への参照です。
   * 基本給の算出に使用されます。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "salary_tier_id")
  private SalaryTier salaryTier;

  /**
   * 雇用形態。
   * 正社員、アルバイト、契約社員等の雇用形態を表します。
   */
  @Column(name = "employment_type", nullable = false)
  private String employmentType;

  /**
   * 所属店舗。
   * 従業員が勤務する店舗への参照です。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  /**
   * 最低保障給与。
   * 従業員に保障される最低給与額（円）です。
   * 歩合給制度における最低保障として使用されます。
   */
  @Column(name = "guaranteed_minimum_salary")
  private Integer guaranteedMinimumSalary;

  /**
   * 管理職手当。
   * 管理職に支給される手当額（円）です。
   */
  @Column(name = "manager_allowance")
  private Integer managerAllowance;

  /**
   * 固定残業時間（分）。
   * みなし残業時間を分単位で格納します。
   * 給与計算時の固定残業代算出に使用されます。
   */
  @Column(name = "fixed_overtime_minutes")
  private Integer fixedOvertimeMinutes;

  /**
   * 歩合減額率。
   * 歩合給から控除される割合を小数点で表現します（例：0.1 = 10%控除）。
   * 社会保険料や福利厚生費の控除計算に使用されます。
   */
  @Column(name = "commission_reduction_rate")
  private BigDecimal commissionReductionRate;

  public Integer getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EmployeeGrade getGrade() {
    return grade;
  }

  public void setGrade(EmployeeGrade grade) {
    this.grade = grade;
  }

  public SalaryTier getSalaryTier() {
    return salaryTier;
  }

  public void setSalaryTier(SalaryTier salaryTier) {
    this.salaryTier = salaryTier;
  }

  public String getEmploymentType() {
    return employmentType;
  }

  public void setEmploymentType(String employmentType) {
    this.employmentType = employmentType;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public Integer getGuaranteedMinimumSalary() {
    return guaranteedMinimumSalary;
  }

  public void setGuaranteedMinimumSalary(Integer guaranteedMinimumSalary) {
    this.guaranteedMinimumSalary = guaranteedMinimumSalary;
  }

  public Integer getManagerAllowance() {
    return managerAllowance;
  }

  public void setManagerAllowance(Integer managerAllowance) {
    this.managerAllowance = managerAllowance;
  }

  public Integer getFixedOvertimeMinutes() {
    return fixedOvertimeMinutes;
  }

  public void setFixedOvertimeMinutes(Integer fixedOvertimeMinutes) {
    this.fixedOvertimeMinutes = fixedOvertimeMinutes;
  }

  public BigDecimal getCommissionReductionRate() {
    return commissionReductionRate;
  }

  public void setCommissionReductionRate(BigDecimal commissionReductionRate) {
    this.commissionReductionRate = commissionReductionRate;
  }
}
