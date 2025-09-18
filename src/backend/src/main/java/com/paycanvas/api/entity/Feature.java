package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 機能マスターエンティティクラス。
 *
 * <p>このクラスはアプリケーション内で利用可能な機能を管理するエンティティです。
 * データベースの「m_features」テーブルにマッピングされ、各機能の基本的な情報を格納します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>システムで利用可能な機能の一覧管理</li>
 *   <li>機能の有効/無効制御の基盤データ</li>
 *   <li>企業単位での機能利用制御（CompanyFeatureエンティティとの連携）</li>
 * </ul>
 */
@Entity
@Table(name = "m_features")
public class Feature {
  /**
   * 機能ID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 機能キー。
   * システム内で機能を識別するための一意のキー文字列です。
   * プログラム内での機能判定に使用されます。
   */
  @Column(name = "feature_key", nullable = false, unique = true)
  private String featureKey;

  /**
   * 機能名。
   * ユーザー向けに表示される機能の名称です。
   */
  @Column(nullable = false)
  private String name;

  /**
   * 機能説明。
   * 機能の詳細な説明や用途を記述するテキストです。
   */
  @Column
  private String description;

  public Integer getId() {
    return id;
  }

  public String getFeatureKey() {
    return featureKey;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
