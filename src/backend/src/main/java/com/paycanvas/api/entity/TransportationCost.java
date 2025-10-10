package com.paycanvas.api.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
/**
 * 従業員交通費マスターエンティティクラス。
 *
 * <p>このクラスは従業員の交通費情報を管理するエンティティです。
 * データベースの「m_transportation_costs」テーブルにマッピングされ、
 * 従業員の月額交通費、通勤経路、適用期間を格納します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>従業員の通勤手当管理</li>
 *   <li>月額交通費の自動計算</li>
 *   <li>通勤経路の記録</li>
 *   <li>給与計算時の交通費支給</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "m_transportation_costs")
public class TransportationCost {
  /**
   * 交通費ID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 従業員。
   * この交通費が適用される従業員への参照です。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;

  /**
   * 月額交通費。
   * 従業員に支給される月額の交通費（円）です。
   */
  @Column(name = "monthly_amount", nullable = false)
  private Integer monthlyAmount;

  /**
   * 通勤経路。
   * 従業員の通勤経路を格納します（例：「自宅→広島駅→陽光台駅→陽光台店」）。
   */
  @Column(name = "route", columnDefinition = "TEXT")
  private String route;

  /**
   * 適用開始日。
   * この交通費設定の適用開始日です。
   */
  @Column(name = "effective_from", nullable = false)
  private LocalDate effectiveFrom;

  /**
   * 適用終了日。
   * この交通費設定の適用終了日です。nullの場合は無期限です。
   */
  @Column(name = "effective_to")
  private LocalDate effectiveTo;

  /**
   * 作成日時。
   * このレコードの作成日時です。
   */
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  /**
   * 更新日時。
   * このレコードの最終更新日時です。
   */
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (effectiveFrom == null) {
      effectiveFrom = LocalDate.now();
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
