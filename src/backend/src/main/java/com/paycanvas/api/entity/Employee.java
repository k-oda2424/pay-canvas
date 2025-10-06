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
import java.time.LocalDate;

import lombok.Data;
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
@Data
@Entity
@Table(name = "m_employees")
public class Employee implements ICompanyEntity {
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
   * 企業ごとの表示用ID。
   * 企業内で1から始まる連番で、ユーザーが編集可能です。
   * (company_id, display_id) の組み合わせで一意になります。
   */
  @Column(name = "display_id", nullable = false)
  private Integer displayId;

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
   * 勤務パターン。
   * 従業員の勤務形態（週休2日など）を表します。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_pattern_id")
  private WorkPattern workPattern;

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
   */
  @Column(name = "fixed_overtime_minutes")
  private Integer fixedOvertimeMinutes;

  /**
   * 歩合減額率。
   * 歩合給から控除される割合を小数点で表現します（例：0.1 = 10%控除）。
   */
  @Column(name = "commission_reduction_rate")
  private BigDecimal commissionReductionRate;

  /**
   * 役員フラグ。
   * true の場合、役員報酬の計算ロジックが適用されます。
   */
  @Column(name = "is_board_member")
  private Boolean isBoardMember = false;

  /**
   * 役員報酬。
   * 役員に支払われる固定報酬額です。
   */
  @Column(name = "board_compensation")
  private Integer boardCompensation;

  /**
   * 入社日。
   * 従業員の入社日を格納します。
   */
  @Column(name = "hire_date")
  private java.time.LocalDate hireDate;

  /**
   * 扶養親族等の数。
   * 所得税の源泉徴収計算に使用されます。
   */
  @Column(name = "number_of_dependents")
  private Integer numberOfDependents = 0;

  /**
   * 住民税月額。
   * 個人ごとに異なる住民税の月額控除額です。
   */
  @Column(name = "resident_tax_monthly")
  private Integer residentTaxMonthly = 0;

    /**
     * 健康保険の標準報酬月額（社会保険事務所通知ベース）
     */
    @Column(name = "health_insurance_standard_amount")
    private Integer healthInsuranceStandardAmount;

    /**
     * 厚生年金保険の標準報酬月額（社会保険事務所通知ベース）
     */
    @Column(name = "pension_insurance_standard_amount")
    private Integer pensionInsuranceStandardAmount;

    /**
     * 退職日（論理削除用）
     * NULL: 在職中
     * 日付: 退職済み
     */
    @Column(name = "resignation_date")
    private LocalDate resignationDate;
}
