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
import java.time.Instant;

/**
 * リフレッシュトークンエンティティクラス。
 *
 * <p>このクラスはJWT認証におけるリフレッシュトークンを管理するエンティティです。
 * データベースの「refresh_tokens」テーブルにマッピングされ、
 * アクセストークンの更新時に使用される長期有効なトークン情報を格納します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>アクセストークンの自動更新</li>
 *   <li>セッションの永続化</li>
 *   <li>セキュアなログアウト処理</li>
 *   <li>トークンの期限管理</li>
 * </ul>
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
  /**
   * リフレッシュトークンID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * トークン所有者。
   * このリフレッシュトークンを所有するユーザーアカウントへの参照です。
   * 遅延読み込み（LAZY）で設定されています。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserAccount user;

  /**
   * リフレッシュトークン文字列。
   * クライアントがアクセストークン更新時に使用する一意のトークン値です。
   * セキュリティのため、データベース全体で一意である必要があります。
   */
  @Column(nullable = false, unique = true)
  private String token;

  /**
   * 有効期限。
   * リフレッシュトークンの有効期限を示すタイムスタンプです。
   * この時刻を過ぎたトークンは無効となります。
   */
  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  /**
   * 作成日時。
   * リフレッシュトークンが作成された日時です。
   * デフォルトで現在時刻が設定されます。
   */
  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  public Integer getId() {
    return id;
  }

  public UserAccount getUser() {
    return user;
  }

  public void setUser(UserAccount user) {
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant expiresAt) {
    this.expiresAt = expiresAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
