# バグ修正履歴

このドキュメントは、PayCanvasプロジェクトで発生した重要なバグとその修正内容を記録します。

## 2025-10-06: ユーザーロール読み込み問題

**コミット**: `171def6e6c47610c910875f53d7de78160184ae7`

### 問題の概要

従業員店舗距離マスタ画面(`/staff/employee-store-distances`)にアクセスすると、ログイン画面に強制的にリダイレクトされる問題が発生しました。

### 症状

- ログイン後、マスタ管理画面へのアクセスが403 Forbiddenエラーとなる
- `/api/masters/**`エンドポイントへのアクセスが拒否される
- JWTトークンには`STAFF`ロールが設定されるが、本来は`COMPANY_ADMIN`ロールであるべき

### 根本原因

1. **JPA @EntityGraphの制限**: `UserRepository.findByEmail()`メソッドで使用していた`@EntityGraph(attributePaths = {"roles", "roles.role", "company"})`が、`UserRole`エンティティの複合主キー(`@EmbeddedId`)と`@MapsId`アノテーションの組み合わせで正しく機能しませんでした。

2. **循環参照によるStackOverflowError**: `UserRole`エンティティで使用していたLombokの`@Data`アノテーションが、`UserAccount`との双方向関係で`hashCode()`の循環呼び出しを引き起こしました。

3. **ロールの未ロード**: 結果として、ログイン時に`user.getRoles().size()`が常に0を返し、デフォルトの`STAFF`ロールが割り当てられていました。

### 調査プロセス

1. **バックエンドログの確認**: ログイン時のデバッグログで`Roles count: 0`を発見
2. **データベース検証**: `m_user_roles`テーブルには正しくデータが存在することを確認
3. **Hibernate SQLログ有効化**: `spring.jpa.show-sql=true`でHibernateが実行するSQLを確認
4. **クエリ分析**: `@EntityGraph`が生成するJOIN FETCHクエリが正しく動作していないことを特定

### 修正内容

#### 1. UserRepository.java
```java
// 修正前
@EntityGraph(attributePaths = {"roles", "roles.role", "company"})
Optional<UserAccount> findByEmail(String email);

// 修正後
@Query("SELECT u FROM UserAccount u LEFT JOIN FETCH u.company WHERE u.email = :email")
Optional<UserAccount> findByEmail(@Param("email") String email);
```

#### 2. UserRoleRepository.java
```java
// 新規追加
@Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.user.id = :userId")
List<UserRole> findByUserIdWithRole(@Param("userId") Integer userId);
```

#### 3. AuthService.java
```java
// 修正後: ログイン処理でロールを明示的にロード
List<UserRole> userRoles = userRoleRepository.findByUserIdWithRole(user.getId());
user.setRoles(new HashSet<>(userRoles));
```

#### 4. UserRole.java
```java
// 修正前
@Data
@Entity
public class UserRole { ... }

// 修正後: @Dataを削除し、カスタムequals/hashCodeを実装
@Entity
public class UserRole {
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
```

### 検証結果

修正後の動作確認:
- ✅ ログイン成功: `COMPANY_ADMIN`ロールが正しく割り当てられる
- ✅ APIアクセス成功: `/api/masters/employee-store-distances`が200 OKを返す
- ✅ JWT認証: トークンに`ROLE_COMPANY_ADMIN`権限が含まれる
- ✅ ログ確認: `DEBUG: Roles count: 1`, `DEBUG: Role: COMPANY_ADMIN`

### 学んだ教訓

1. **JPA複合キーの制限**: `@EmbeddedId`と`@MapsId`を使用したエンティティでは、`@EntityGraph`が期待通りに動作しない場合がある。明示的なJOIN FETCHクエリを使用する方が確実。

2. **Lombokの注意点**: 双方向関係を持つエンティティでは`@Data`アノテーションを避け、カスタム`equals()`/`hashCode()`を実装すべき。

3. **デバッグの重要性**: SQLログを有効化し、Hibernateが実際に実行しているクエリを確認することで、問題の特定が容易になる。

4. **段階的なテスト**: データベース直接確認 → SQLログ確認 → アプリケーションログ確認の順で調査することで、問題の箇所を特定できる。

### 関連ドキュメント

- [CLAUDE.md - 技術的な注意事項](/CLAUDE.md#技術的な注意事項)
- Spring Data JPA ドキュメント: [@EntityGraph](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.entity-graph)
- Hibernate ドキュメント: [Composite Identifiers](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#identifiers-composite)
