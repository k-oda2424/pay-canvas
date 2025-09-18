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

### エンティティ関係
- **デフォルト企業**: ID 1はシングルテナント操作用のデフォルト企業として使用
- **遅延読み込み**: パフォーマンスのため、ほとんどのエンティティ関係は`FetchType.LAZY`を使用
- **カスケード操作**: 意図しない削除を防ぐため慎重に管理