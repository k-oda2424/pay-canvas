package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ロールマスターエンティティクラス。
 *
 * <p>このクラスはシステム内で利用可能なロール（役割）を管理するエンティティです。
 * データベースの「m_roles」テーブルにマッピングされ、ユーザーの権限管理に使用されます。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>ユーザーの権限レベル定義</li>
 *   <li>アクセス制御の基盤データ</li>
 *   <li>機能別の操作権限管理</li>
 *   <li>組織階層での権限付与</li>
 * </ul>
 */
@Entity
@Table(name = "m_roles")
public class Role {
  /**
   * ロールID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * ロールキー。
   * システム内でロールを識別するための一意のキー文字列です。
   * プログラム内での権限判定に使用されます（例: "ADMIN", "MANAGER", "USER"）。
   */
  @Column(name = "role_key", nullable = false, unique = true)
  private String roleKey;

  /**
   * ロール名。
   * ユーザー向けに表示されるロールの名称です。
   * 管理画面や設定画面での表示に使用されます。
   */
  @Column(nullable = false)
  private String name;

  public Integer getId() {
    return id;
  }

  public String getRoleKey() {
    return roleKey;
  }

  public String getName() {
    return name;
  }
}
