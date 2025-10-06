package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
/**
 * ユーザーロール関連エンティティクラス。
 *
 * <p>このクラスはユーザーアカウントとロールの関連を管理するエンティティです。
 * データベースの「m_user_roles」テーブルにマッピングされ、
 * ユーザーとロールの多対多関係を表現する中間テーブルとして機能します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>ユーザーへのロール割り当て管理</li>
 *   <li>複数ロール保持ユーザーの権限管理</li>
 *   <li>動的な権限変更対応</li>
 *   <li>ロールベースアクセス制御（RBAC）の実装</li>
 * </ul>
 *
 * <p>複合主キー（UserRoleId）を使用して、ユーザーIDとロールIDの組み合わせで一意性を保証します。</p>
 */
@Entity
@Table(name = "m_user_roles")
public class UserRole {
  /**
   * 複合主キー。
   * ユーザーIDとロールIDの組み合わせで構成される埋め込み主キーです。
   */
  @EmbeddedId private UserRoleId id = new UserRoleId();

  /**
   * ユーザーアカウント。
   * ロールが割り当てられるユーザーへの参照です。
   * @MapsIdアノテーションにより複合主キーのuserIdフィールドと連動します。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private UserAccount user;

  /**
   * ロール。
   * ユーザーに割り当てられるロールへの参照です。
   * @MapsIdアノテーションにより複合主キーのroleIdフィールドと連動します。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("roleId")
  @JoinColumn(name = "role_id")
  private Role role;

  public UserRoleId getId() {
    return id;
  }

  public void setId(UserRoleId id) {
    this.id = id;
  }

  public UserAccount getUser() {
    return user;
  }

  public void setUser(UserAccount user) {
    this.user = user;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserRole userRole = (UserRole) o;
    return id != null && id.equals(userRole.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
