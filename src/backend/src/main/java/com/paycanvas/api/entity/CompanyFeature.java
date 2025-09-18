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
 * 企業機能設定エンティティクラス。
 *
 * <p>このクラスは企業ごとの機能の有効/無効を管理するエンティティです。
 * データベースの「m_company_features」テーブルにマッピングされ、
 * 企業と機能の中間テーブルとして機能します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>企業単位での機能制御（機能の有効化/無効化）</li>
 *   <li>SaaS型サービスでの機能制限管理</li>
 *   <li>契約プランに応じた機能提供制御</li>
 *   <li>段階的な機能リリース管理</li>
 * </ul>
 */
@Entity
@Table(name = "m_company_features")
public class CompanyFeature {
  /**
   * 企業機能設定ID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 対象企業。
   * 機能設定を適用する企業への参照です。
   * 遅延読み込み（LAZY）で設定されています。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  /**
   * 対象機能。
   * 有効/無効を設定する機能への参照です。
   * 遅延読み込み（LAZY）で設定されています。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feature_id", nullable = false)
  private Feature feature;

  /**
   * 機能有効フラグ。
   * この企業でこの機能が有効かどうかを示します。
   * true: 有効、false: 無効
   */
  @Column(name = "is_enabled", nullable = false)
  private boolean enabled;

  public Integer getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Feature getFeature() {
    return feature;
  }

  public void setFeature(Feature feature) {
    this.feature = feature;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
