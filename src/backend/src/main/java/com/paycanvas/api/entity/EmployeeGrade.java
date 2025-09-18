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
 * 従業員等級マスターエンティティクラス。
 *
 * <p>このクラスは企業ごとの従業員等級情報を管理するエンティティです。
 * データベースの「m_employee_grades」テーブルにマッピングされ、
 * 従業員の職位・等級と歩合率の設定を格納します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>従業員の等級・職位管理</li>
 *   <li>等級別歩合率の設定</li>
 *   <li>昇進・昇格時の等級変更管理</li>
 *   <li>給与計算時の歩合率適用</li>
 * </ul>
 */
@Entity
@Table(name = "m_employee_grades")
public class EmployeeGrade {
  /**
   * 従業員等級ID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 所属企業。
   * この等級が設定されている企業への参照です。
   * 企業ごとに独自の等級体系を持つことができます。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  /**
   * 等級名。
   * 従業員等級の名称です（例：「主任」「係長」「課長」など）。
   */
  @Column(name = "grade_name", nullable = false)
  private String gradeName;

  /**
   * 歩合率。
   * この等級の従業員に適用される歩合率です。
   * 小数値で格納され、給与計算時の歩合給算出に使用されます。
   */
  @Column(name = "commission_rate", nullable = false)
  private double commissionRate;

  public Integer getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getGradeName() {
    return gradeName;
  }

  public void setGradeName(String gradeName) {
    this.gradeName = gradeName;
  }

  public double getCommissionRate() {
    return commissionRate;
  }

  public void setCommissionRate(double commissionRate) {
    this.commissionRate = commissionRate;
  }
}
