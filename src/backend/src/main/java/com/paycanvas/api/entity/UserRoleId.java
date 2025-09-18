package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * ユーザーロール複合主キークラス。
 *
 * <p>このクラスはUserRoleエンティティの複合主キーを定義します。
 * JPAの@Embeddableアノテーションにより埋め込み可能型として設定され、
 * ユーザーIDとロールIDの組み合わせで一意性を保証します。</p>
 *
 * <p>Serializableインターフェースを実装し、equals()とhashCode()メソッドを
 * 適切にオーバーライドすることで、JPA仕様に準拠した複合主キーとして機能します。</p>
 */
@Embeddable
public class UserRoleId implements Serializable {
  /**
   * ユーザーID。
   * UserAccountエンティティの主キーへの参照です。
   */
  @Column(name = "user_id")
  private Integer userId;

  /**
   * ロールID。
   * Roleエンティティの主キーへの参照です。
   */
  @Column(name = "role_id")
  private Integer roleId;

  /**
   * デフォルトコンストラクタ。
   * JPAの要求により必要です。
   */
  public UserRoleId() {}

  /**
   * パラメータ付きコンストラクタ。
   * ユーザーIDとロールIDを指定して複合主キーを初期化します。
   *
   * @param userId ユーザーID
   * @param roleId ロールID
   */
  public UserRoleId(Integer userId, Integer roleId) {
    this.userId = userId;
    this.roleId = roleId;
  }

  public Integer getUserId() {
    return userId;
  }

  public Integer getRoleId() {
    return roleId;
  }

  /**
   * オブジェクトの等価性を判定します。
   * ユーザーIDとロールIDの両方が一致する場合にtrueを返します。
   *
   * @param o 比較対象のオブジェクト
   * @return 等価の場合true、そうでなければfalse
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserRoleId that = (UserRoleId) o;
    return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
  }

  /**
   * オブジェクトのハッシュコードを計算します。
   * ユーザーIDとロールIDを組み合わせてハッシュ値を生成します。
   *
   * @return ハッシュコード値
   */
  @Override
  public int hashCode() {
    return Objects.hash(userId, roleId);
  }
}
