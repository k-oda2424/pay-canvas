# payCanvas デプロイチェックリスト

## 1. 事前準備
- [ ] `.env` / 環境変数に本番設定（DB接続、JWTシークレット等）を登録
- [ ] Gradle Wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/*`) が同梱されていることを確認
- [ ] `docker-compose.yml` が本番向けでない場合、別途インフラ構成（KubernetesやマネージドDB）を用意

## 2. バックエンド
- [ ] PostgreSQL（本番）を準備し、接続情報を環境変数に設定
- [ ] Flyway マイグレーションを実行
  ```bash
  cd src/backend
  ./gradlew flywayMigrate
  ```
- [ ] バックエンドアプリをビルド
  ```bash
  cd src/backend
  ./gradlew clean build
  ```
- [ ] 実行用JAR（`build/libs/paycanvas-api-*.jar`）をサーバへ配置し、`java -jar` で起動
- [ ] プロセス管理（systemd, supervisor, Docker, Kubernetes等）を設定

## 3. フロントエンド
- [ ] APIエンドポイント（`/api` プロキシ）を本番URLに合わせて調整
- [ ] 本番用ビルドを作成
  ```bash
  npm run build
  ```
- [ ] `dist/` を静的ホスティング（S3+CloudFront等）やNode.jsサーバで配信
- [ ] ベーシックなSmokeテストをPlaywrightで実行
  ```bash
  npx playwright install
  npm run test:ui
  ```

## 4. 運用監視
- [ ] CloudWatch/Grafanaなどでアプリ・DBのメトリクス監視を設定
- [ ] アプリログの集約（例: CloudWatch Logs, ELK, Loki）
- [ ] 主要アラート（レスポンスタイム、ジョブ失敗、APIエラー率）を設定

## 5. リリース後
- [ ] 主要フロー（ログイン→ダッシュボード→日次実績→給与計算→給与明細）の動作確認
- [ ] スーパー管理者・一般スタッフの権限が期待通りか検証
- [ ] バックアップ/ローテーションポリシー確認
