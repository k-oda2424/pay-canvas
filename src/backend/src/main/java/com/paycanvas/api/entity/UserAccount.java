package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * ユーザーアカウントエンティティクラス。
 *
 * <p>このクラスはシステムにログインするユーザーアカウント情報を管理するエンティティです。
 * データベースの「m_users」テーブルにマッピングされ、
 * ユーザーの認証情報、プロフィール、権限などを格納します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>ユーザー認証（ログイン/ログアウト）</li>
 *   <li>ロールベースアクセス制御</li>
 *   <li>ユーザープロフィール管理</li>
 *   <li>企業単位でのユーザー管理</li>
 * </ul>
 */
@Entity
@Table(name = "m_users")
public class UserAccount {
  /**
   * ユーザーID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 所属企業。
   * このユーザーが所属する企業への参照です。
   * マルチテナントアーキテクチャにおいてテナント分離に使用されます。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  /**
   * メールアドレス。
   * ユーザーのメールアドレスで、ログインIDとして使用されます。
   * システム内で一意である必要があります。
   */
  @Column(nullable = false, unique = true)
  private String email;

  /**
   * パスワードハッシュ。
   * ユーザーのパスワードをハッシュ化した値です。
   * セキュリティのため、平文パスワードは保存されません。
   */
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  /**
   * 表示名。
   * システム内で表示されるユーザー名です。
   * メールアドレスとは別に、ユーザーが設定できる表示用の名前です。
   */
  @Column(name = "display_name", nullable = false)
  private String displayName;

  /**
   * アカウントステータス。
   * ユーザーアカウントの状態を表します（例："ACTIVE", "INACTIVE", "SUSPENDED"など）。
   * アカウントの有効/無効判定に使用されます。
   */
  @Column(nullable = false)
  private String status;

  /**
   * ユーザーロールセット。
   * このユーザーに割り当てられたロールのコレクションです。
   * UserRoleエンティティとの1対多関係で、遅延読み込みで設定されています。
   */
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<UserRole> roles = new HashSet<>();

  public Integer getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Set<UserRole> getRoles() {
    return roles;
  }

  /**
   * ユーザーにロールを追加します。
   * 指定されたUserRoleオブジェクトをこのユーザーのロールセットに追加します。
   *
   * @param role 追加するUserRoleオブジェクト
   */
  public void addRole(UserRole role) {
    roles.add(role);
  }
}
