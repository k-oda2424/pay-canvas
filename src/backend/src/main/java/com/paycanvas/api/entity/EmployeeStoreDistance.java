package com.paycanvas.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * 従業員店舗距離マスタエンティティクラス。
 *
 * <p>このクラスは従業員の自宅から各店舗への距離と通勤手段を管理するエンティティです。
 * データベースの「m_employee_store_distances」テーブルにマッピングされます。</p>
 *
 * <p>主な機能：</p>
 * <ul>
 *   <li>各従業員の自宅から各店舗への距離を管理</li>
 *   <li>各店舗への通勤手段を管理</li>
 *   <li>店舗配置転換時の交通費計算に使用</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "m_employee_store_distances")
public class EmployeeStoreDistance implements ICompanyEntity {
  /**
   * ID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 所属企業。
   * この距離データが所属する企業への参照です。
   * 遅延読み込み（LAZY）で設定されています。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  /**
   * 従業員。
   * 距離データの対象となる従業員への参照です。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;

  /**
   * 店舗。
   * 距離データの対象となる店舗への参照です。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  /**
   * 通勤距離（km）。
   * 従業員の自宅からこの店舗までの片道距離です。
   */
  @Column(name = "distance_km", nullable = false, precision = 5, scale = 1)
  private BigDecimal distanceKm;

  /**
   * 通勤手段。
   * この店舗への通勤に使用する手段への参照です。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "commute_method_id", nullable = false)
  private CommuteMethod commuteMethod;

  /**
   * 作成日時。
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  /**
   * 更新日時。
   */
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  /**
   * エンティティ作成時に自動的に日時を設定します。
   */
  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  /**
   * エンティティ更新時に自動的に更新日時を設定します。
   */
  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

  @Override
  public Integer getDisplayId() {
    // display_idを持たないため、idを返す
    return id;
  }

  @Override
  public void setDisplayId(Integer displayId) {
    // display_idを持たないため、何もしない
  }
}
