# CLAUDE.md

このファイルはClaude Code (claude.ai/code)がこのリポジトリで作業する際のガイドラインです。

## プロジェクト概要

PayCanvasは美容室向けのSaaS型給与計算システムで、マルチテナント対応しています。勤怠・売上データから手当・歩合を含む給与を算出します。

**アーキテクチャ**: Vite + Reactフロントエンド、Spring Bootバックエンド、PostgreSQLデータベース

## 開発コマンド

### プロジェクトセットアップ
```bash
# フロントエンド依存関係のインストール
npm install

# PostgreSQLコンテナ起動
docker compose up -d postgres

# バックエンド起動（プロジェクトルートから）
cd src/backend && ./gradlew bootRun

# フロントエンド起動（プロジェクトルートから）
npm run dev
```

### サービス再起動（カスタムスクリプト）
```bash
# 両方のサービスを再起動
./restart.sh

# フロントエンドのみ再起動
./restart.sh f

# バックエンドのみ再起動
./restart.sh b
```

### テスト実行
```bash
# フロントエンドビルドチェック
npm run build

# Playwright E2Eテスト（初回はインストール必要）
npx playwright install
npm run test:ui

# バックエンドテストとビルド
cd src/backend && ./gradlew clean build
```

## アーキテクチャ・コード構成

### マルチテナントアーキテクチャ
- **企業ベースのテナント管理**: 各企業（美容室）は分離されている
- **ロールベースアクセス制御**: SUPER_ADMIN、COMPANY_ADMIN、STAFFロール
- **機能トグル**: `CompanyFeature`エンティティを通じて企業ごとに機能を有効/無効化

### ⚠️ マルチテナント分離の厳格遵守（重要）
**絶対に他企業のデータが見えたり、操作できたりしてはなりません。これはセキュリティ上の最重要事項です。**

#### 必須チェックリスト
全てのサービス層メソッドで以下を必ず実装してください：

1. **一覧取得メソッド**
   - `findAll()`や`findAllWithRelations()`の結果を必ず企業IDでフィルタリング
   - 例: `.filter(e -> e.getCompany() != null && e.getCompany().getId() == DEFAULT_COMPANY_ID)`
   - リポジトリレベルでフィルタリングする場合は、`findByCompanyId(Integer companyId)`のようなメソッドを使用

2. **参照メソッド（findById等）**
   - 取得後に必ず企業IDをチェック
   - 権限がない場合は`ResponseStatusException(HttpStatus.FORBIDDEN)`をスロー
   - 例: `if (entity.getCompany().getId() != DEFAULT_COMPANY_ID) throw new ResponseStatusException(...)`

3. **更新・削除メソッド**
   - 対象データの企業IDを必ず検証
   - 他企業のデータを更新・削除できないようにする

4. **作成メソッド**
   - 新規作成時は必ず正しい企業IDを設定
   - 例: `entity.setCompany(defaultCompany())`

#### 実装例
```java
// ❌ 危険: 全企業のデータを返してしまう
public List<Employee> listEmployees() {
  return employeeRepository.findAll();
}

// ✅ 正しい: 自企業のデータのみ返す
public List<Employee> listEmployees() {
  return employeeRepository.findAllWithRelations().stream()
      .filter(e -> e.getCompany() != null && e.getCompany().getId() == DEFAULT_COMPANY_ID)
      .map(this::toMaster)
      .toList();
}

// ✅ さらに良い: リポジトリレベルでフィルタリング
public List<Employee> listEmployees() {
  return employeeRepository.findByCompanyId(DEFAULT_COMPANY_ID);
}
```

#### コードレビュー時の確認事項
- 新しいサービスメソッドを追加する際は、必ずマルチテナント分離をチェック
- 既存コードを修正する際も、企業IDフィルタリングが抜けていないか確認
- テストデータで複数企業を作成し、データ漏洩がないか必ずテストすること

### 認証システム
- **JWT + リフレッシュトークン**: アクセストークン（1時間）+ リフレッシュトークン（14日間）
- **トークンローテーション**: セキュリティのため、リフレッシュトークンは使用時に更新
- **サービス**: ログイン/更新用の`AuthService`、トークン生成/検証用の`JwtService`

### データベーススキーマ（Flyway V1-V6）
- **コアエンティティ**: Company、UserAccount、Employee、Store
- **マスターデータ**: EmployeeGrade、SalaryTier、Feature
- **セキュリティ**: Role、UserRole、RefreshToken、CompanyFeature

### APIアーキテクチャ
- **RESTコントローラー**: `com.paycanvas.api.controller`パッケージ
- **サービス層**: `com.paycanvas.api.service`のビジネスロジック
- **JPAエンティティ**: `com.paycanvas.api.entity`の適切な関連付け
- **リクエスト/レスポンスモデル**: `com.paycanvas.api.model`パッケージ

### フロントエンドAPIクライアント
- **ベースURL管理**: 全APIコールは`API_BASE_URL`経由で相対パスを使用
- **HTTPメソッド**: `src/api/client.ts`の`apiGet`、`apiPost`、`apiPut`を使用
- **認証**: JWTトークンは自動的にリクエストに含まれる

## 開発環境

### デフォルトアクセス
- フロントエンド: http://localhost:5173
- バックエンド: http://localhost:8080
- PostgreSQL: localhost:5432 (ユーザー: paycanvas、パスワード: paycanvas、DB: paycanvas_dev)

### テストユーザー
| ロール | メールアドレス | パスワード |
|--------|---------------|-----------|
| スーパー管理者 | super@paycanvas.io | password |
| 会社管理者 | admin@paycanvas.io | password |
| スタッフ | staff@paycanvas.io | password |

### データベース接続
```bash
# PostgreSQL接続
PGPASSWORD=paycanvas psql -h localhost -U paycanvas -d paycanvas_dev
```

## コード規約

### 動作確認要件
**全ての実装・修正後は必ず動作確認を行うこと。これは絶対に省略してはなりません。**

#### 必須確認項目
1. **バックエンド実装後**
   - バックエンドが正常に起動すること
   - 該当APIエンドポイントが正常にレスポンスを返すこと
   - データベースの変更が意図通りに反映されていること
   - ログにエラーが出ていないこと

2. **フロントエンド実装後**
   - フロントエンドが正常に起動すること
   - ブラウザで該当画面にアクセスできること
   - 実装した機能が正常に動作すること
   - コンソールにエラーが出ていないこと

3. **データベースマイグレーション後**
   - Flywayマイグレーションが正常に実行されること
   - テーブル・カラムが意図通りに作成されていること
   - 既存データに影響がないこと

4. **統合テスト**
   - フロントエンドからバックエンドまで一連の操作が正常に動作すること
   - テストユーザーでログインして実際の画面操作を確認すること

#### 確認方法
- **手動確認**: ブラウザで http://localhost:5173 にアクセスし、実際に操作する
- **APIテスト**: curlまたはブラウザDevToolsのNetworkタブでAPIレスポンスを確認
- **ログ確認**: バックエンドログとブラウザコンソールを確認
- **データベース確認**: psqlで直接データを確認

### JavaDoc要件
**全てのクラスと関数には日本語のJavaDocコメントが必須です**。以下に適用：
- サービスクラス: メソッドの目的、パラメータ、戻り値、例外を含む
- コントローラークラス: エンドポイント説明、HTTPメソッド、レスポンス形式を含む
- エンティティクラス: データベース関係、JPAアノテーション、フィールドの目的を含む
- `@param`、`@return`、`@throws`タグを適切に使用

### API一貫性
- **HTTPメソッド**: 更新にはPUTを使用（PATCHではない）
- **エラーハンドリング**: 適切なHTTPステータスコードで`ResponseStatusException`を使用
- **バリデーション**: コントローラーでリクエスト検証に`@Valid`を使用
- **トランザクション管理**: サービスで適切に`@Transactional`を使用

### サービスクラス設計
**全てのサービスクラスはCustomServiceを継承し、CRUD操作には必ずCustomServiceのメソッドを使用すること。**

#### 必須要件
1. **サービスクラスの継承**
   - 全てのサービスクラスは`CustomService<エンティティ型, ID型>`を継承すること
   - 例: `public class EmployeeService extends CustomService<Employee, Integer>`

2. **CRUD操作の実装**
   - **検索（一覧取得）**: `find(Class<T> entityClass, Integer companyId)` を使用
   - **取得（ID指定）**: `findById(Class<T> entityClass, Integer id, Integer companyId)` を使用
   - **作成**: `save(T entity, Integer companyId)` を使用
   - **更新**: `update(T entity, Integer companyId)` を使用
   - **削除**: `delete(Class<T> entityClass, Integer id, Integer companyId)` を使用

3. **マルチテナント分離の自動化**
   - CustomServiceは企業IDによるフィルタリングを自動的に行う
   - リポジトリを直接使用する場合も、必ず企業IDでフィルタリングすること

#### 実装例
```java
// ✅ 正しい: CustomServiceを継承し、findメソッドを使用
@Service
public class TransportationCostService extends CustomService<TransportationCost, Integer> {

  public TransportationCostService(TransportationCostRepository repository) {
    super(repository);
  }

  public List<TransportationCostResponse> listByCompanyId(Integer companyId) {
    // CustomServiceのfindメソッドを使用（企業IDフィルタリング自動）
    return find(TransportationCost.class, companyId).stream()
        .map(this::toResponse)
        .toList();
  }

  public TransportationCostResponse getById(Integer id, Integer companyId) {
    // CustomServiceのfindByIdメソッドを使用（企業IDチェック自動）
    TransportationCost cost = findById(TransportationCost.class, id, companyId);
    return toResponse(cost);
  }
}

// ❌ 間違い: リポジトリを直接使用（企業IDフィルタリングなし）
@Service
public class TransportationCostService {

  private final TransportationCostRepository repository;

  public List<TransportationCostResponse> listByCompanyId(Integer companyId) {
    // 危険: CustomServiceを使わずリポジトリ直接呼び出し
    return repository.findByCompanyId(companyId).stream()
        .map(this::toResponse)
        .toList();
  }
}
```

#### 例外的なケース
- 認証関連（AuthService、JwtService）など、企業IDに依存しない処理のみCustomService継承を省略可能
- ただし、マスタデータや業務データを扱う全てのサービスは**必ずCustomService継承が必須**

### エンティティ関係
- **デフォルト企業**: ID 1はシングルテナント操作用のデフォルト企業として使用
- **遅延読み込み**: パフォーマンスのため、ほとんどのエンティティ関係は`FetchType.LAZY`を使用
- **カスケード操作**: 意図しない削除を防ぐため慎重に管理

## メール機能

### メール送信設定
- **ライブラリ**: Spring Mail Sender + Gmail SMTP
- **設定方法**: `.env`ファイルで環境変数管理（開発環境）、AWS Parameter Store（本番環境）
- **送信可能数**: Gmailアカウント 500通/日、Google Workspace 2,000通/日
- **環境変数**:
  - `MAIL_USERNAME`: Gmail送信アカウント
  - `MAIL_PASSWORD`: Gmailアプリパスワード（16文字、スペースなし）
  - `MAIL_FROM_ADDRESS`: 送信元メールアドレス
  - `MAIL_FROM_NAME`: 送信元表示名（PayCanvas）
  - `APP_URL`: メール本文に含まれるアプリケーションURL（開発: `http://localhost:5173`、本番: `https://your-domain.com`）

### メール送信機能
1. **初期パスワード通知** (`EmailService.sendInitialPassword`)
   - スーパー管理者が企業登録時に自動送信
   - 企業管理者アカウント作成と同時にメール送信
   - 内容: 企業名、メールアドレス、初期パスワード、ログインURL

2. **パスワードリセット通知** (`EmailService.sendPasswordResetNotification`)
   - スーパー管理者が企業編集画面からパスワードリセット実行時に送信
   - 内容: 企業名、メールアドレス、新しいパスワード、ログインURL

### デプロイ時の注意
- **開発環境**: `.env`ファイルで管理、`restart-backend-with-env.sh`で起動
- **本番環境**: AWS Parameter Store推奨（詳細は`docs/aws-deployment-guide.md`参照）
- **Gmailアプリパスワード**: 2段階認証有効化後にhttps://myaccount.google.com/apppasswordsで生成
- **本番URL**: 必ず`APP_URL`環境変数を本番ドメインに設定すること

## プロジェクトドキュメント

### 設計書（`docs/`ディレクトリ）
- **要件定義書**: `docs/beauty-salon-payroll-requirements.md`
  - システムの目的、ゴール、ユーザー権限、機能要件の全体像
- **外部設計書**: `docs/beauty-salon-payroll-external-design.md`
  - アーキテクチャ概要、API設計、主要ビジネスロジック
  - DB設計の詳細は`docs/beauty-salon-payroll-db-design.md`を参照と記載
- **DB設計書**: `docs/beauty-salon-payroll-db-design.md`
  - 全テーブル定義（マスタ`m_*`、トランザクション`t_*`）、ER図相当の情報
- **内部設計書**: `docs/beauty-salon-payroll-internal-design.md`
  - 詳細なビジネスロジック、給与計算アルゴリズム
- **現在の実装状況**: `docs/current-implementation-overview.md`
  - 実装済み機能の画面仕様、DB構造、マイグレーション履歴
- **バックエンドデータモデル**: `docs/backend-datamodel.md`
  - JPAエンティティとデータベーステーブルの対応関係

### 運用ガイド
- **AWSデプロイガイド**: `docs/aws-deployment-guide.md`
  - Parameter Store、Secrets Manager、Elastic Beanstalk、ECS/Fargateの設定方法
  - 環境変数管理、メール設定、セキュリティベストプラクティス
- **メールセットアップガイド**: `docs/email-setup-guide.md`
  - Gmailアプリパスワード生成手順、Spring Mail Sender設定方法

### 開発計画
- **詳細ロードマップ**: `docs/detailed-roadmap.md`
- **フェーズ別計画**:
  - `docs/phase1-data-model-expansion.md`: データモデル拡張
  - `docs/phase2-grade-master-extension.md`: 等級マスタ拡張
  - `docs/phase3-employee-autocalc.md`: 従業員自動計算
  - `docs/phase4-store-inline-editing.md`: 店舗インライン編集
  - `docs/phase5-quality-and-release-checklist.md`: 品質とリリースチェックリスト

### テストとQA
- **テスト戦略**: `docs/test-strategy.md`
- **マニュアル検証**: `docs/manual-verification.md`
- **UIプレビューガイド**: `docs/ui-preview-guide.md`

## 現在の実装状況（2025-09-30時点）

### 実装済み機能
1. **認証基盤**
   - JWT + リフレッシュトークン（アクセストークン1時間、リフレッシュトークン14日間）
   - ロール別アクセス制御（SUPER_ADMIN、COMPANY_ADMIN、STAFF）
   - トークンローテーション機能

2. **スーパー管理者機能**
   - 利用企業管理（登録、一覧、編集、企業管理者アカウント作成）
   - 企業管理者パスワードリセット機能
   - 初期パスワードメール送信（企業登録時）
   - パスワードリセットメール送信

3. **会社管理者機能（マスタ管理）**
   - 従業員マスタ（`/staff/employees`）: 登録、一覧、編集、削除
   - 店舗マスタ（`/staff/stores`）: 登録、一覧、編集、削除
   - 等級マスタ（`/staff/grades`）: 登録、一覧、編集、削除
   - 給与プランマスタ（`/staff/salary`）: 登録、一覧、編集、削除

4. **データベース**
   - Flyway V1-V14マイグレーション完了
   - マスタテーブル: Company、UserAccount、Employee、Store、EmployeeGrade、SalaryTier
   - 店舗と従業員の外部キー連携（V7で`store_id`追加）
   - リフレッシュトークンテーブル（V4）

5. **メール機能**
   - Spring Mail Sender + Gmail SMTP統合
   - 初期パスワード通知メール（企業登録時）
   - パスワードリセット通知メール
   - ログインURL自動挿入（環境変数`APP_URL`で制御）

### 未実装機能（今後の開発予定）
- 給与計算エンジン（`PayrollCalculationEngineService`）
- 給与明細生成・閲覧機能
- ダッシュボード（KPI表示）
- 日次データ入力機能（勤怠、売上実績）
- 月次給与計算実行機能
- レポート・データエクスポート機能
- スタッフ向け給与明細閲覧画面

### 技術スタック
- **フロントエンド**: Vite 5.2.0 + React 18.2.0 + TypeScript 5.2.2
- **バックエンド**: Spring Boot 3.2.4 + Java 18
- **データベース**: PostgreSQL 15.13 (Docker Compose)
- **認証**: JWT (jjwt 0.11.5)
- **メール**: Spring Mail Sender + Jakarta Mail 2.0.3
- **マイグレーション**: Flyway 9.22.3
- **テスト**: Playwright (E2E), Gradle (バックエンド)

## 技術的な注意事項

### JPA複合キーエンティティのロード問題

**問題**: `UserRole`エンティティのような`@EmbeddedId`を使用した複合主キーエンティティは、`@EntityGraph`では正しくロードされない場合があります。

**症状**:
- ログイン時にユーザーロールが0件となる
- `user.getRoles().size()`が常に0を返す
- データベースには正しくデータが存在する

**根本原因**:
1. `@MapsId`アノテーションと`@EntityGraph`の組み合わせがHibernateで正しく機能しない
2. `UserAccount`と`UserRole`の双方向関係により、Lombokの`@Data`アノテーションが循環参照を引き起こす

**解決方法**:
```java
// ❌ 動作しない: @EntityGraphでは複合キーの関連が正しくロードされない
@EntityGraph(attributePaths = {"roles", "roles.role", "company"})
Optional<UserAccount> findByEmail(String email);

// ✅ 正しい方法1: UserRepositoryでは会社情報のみをフェッチ
@Query("SELECT u FROM UserAccount u LEFT JOIN FETCH u.company WHERE u.email = :email")
Optional<UserAccount> findByEmail(@Param("email") String email);

// ✅ 正しい方法2: UserRoleRepositoryに専用メソッドを作成
@Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.user.id = :userId")
List<UserRole> findByUserIdWithRole(@Param("userId") Integer userId);

// ✅ 正しい方法3: AuthServiceで明示的にロードする
List<UserRole> userRoles = userRoleRepository.findByUserIdWithRole(user.getId());
user.setRoles(new HashSet<>(userRoles));
```

**循環参照の回避**:
```java
// ❌ 危険: @Dataは双方向関係でStackOverflowErrorを引き起こす
@Data
@Entity
public class UserRole {
  @ManyToOne
  private UserAccount user; // UserAccountもrolesフィールドを持つ
}

// ✅ 安全: カスタムequals/hashCodeで循環参照を回避
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

**参考**: コミット `171def6e6c47610c910875f53d7de78160184ae7`