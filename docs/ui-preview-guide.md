# payCanvas UI プレビュー手順

## 前提
- Node.js 18 以上
- Docker / Docker Compose
- Gradle (ローカルに未導入の場合は `brew install gradle` などでセットアップ) もしくは `src/backend` ディレクトリで `gradle wrapper --gradle-version 8.6` を実行して Gradle Wrapper を生成

## 起動手順
1. 依存パッケージをインストール
   ```bash
   npm install
   ```
2. PostgreSQL コンテナを起動
   ```bash
   make db-up
   ```
3. バックエンド API を起動 (別ターミナル) ※初回は `src/backend` で `gradle wrapper --gradle-version 8.6` を実行してから `./gradlew bootRun`
   ```bash
   make dev-back
   ```
4. フロントエンドを起動 (別ターミナル)
   ```bash
   npm run dev
   ```
5. ブラウザで `http://localhost:5173` を開き、以下のサンプルアカウントでログイン

| ロール | メールアドレス | パスワード |
| --- | --- | --- |
| 会社管理者 | `admin@paycanvas.io` | `password` |
| スーパー管理者 | `super@paycanvas.io` | `password` |
| 一般スタッフ | `staff@paycanvas.io` | `password` |

## 画面概要
- **ダッシュボード**: KPI、対応タスク、お知らせを表示
- **日次実績**: 勤怠、店舗売上、個人売上を一覧表示
- **給与計算**: 月次給与計算ジョブ実行と履歴
- **給与明細**: 従業員ごとの支給情報
- **マスタ管理**: 従業員マスタ一覧
- **機能設定**: スーパー管理者による機能フラグ切り替え

## よくある質問
- バックエンドのビルドで Gradle が見つからない場合、ローカルに Gradle をインストールしてください。もしくは `src/backend` ディレクトリで `gradle wrapper --gradle-version 8.6` を実行し、Gradle Wrapper を生成してください（初回のみ）。
- ポート衝突が発生した場合は `vite.config.ts` や `application.properties` を調整してください。
- Playwright を使用したUIテストを実行する場合は、事前に `npx playwright install` を実行してください。バックエンド/フロントエンドのサーバーを起動した状態で `npm run test:ui` を実行します。
