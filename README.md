# payCanvas

美容室向けSaaS型給与計算システムのリポジトリです。複数テナントに対応し、勤怠・売上データから手当・歩合を含む給与を算出します。フロントエンドは Vite + React、バックエンドは Spring Boot、DB は PostgreSQL を採用しています。

## セットアップ

### 前提
- Node.js 18 以上
- Docker / Docker Compose
- Java 17

### 手順
1. 依存関係のインストール
   ```bash
   npm install
   ```
2. PostgreSQL コンテナを起動
   ```bash
   docker compose up -d postgres
   ```
3. バックエンドを起動
   ```bash
   cd src/backend
   ./gradlew bootRun
   ```
4. フロントエンドを起動
   ```bash
   npm run dev
   ```
5. ブラウザで `http://localhost:5173` を開き、以下のサンプルでログインできます。

| ロール           | メールアドレス           | パスワード |
|------------------|--------------------------|------------|
| スーパー管理者   | super@paycanvas.io       | password   |
| 会社管理者       | admin@paycanvas.io       | password   |
| 一般スタッフ     | staff@paycanvas.io       | password   |

## 主な機能
- ロールベースの認証（JWT＋リフレッシュトークン）
- マスタ管理（従業員・店舗・等級・給与プラン）
- 日次実績・給与計算・明細/レポート（実装中）
- スーパー管理者による会社管理者ユーザー登録

## テスト
- フロントエンド：`npm run build`
- Playwright（ローカルで）: `npx playwright install` → `npm run test:ui`
- バックエンド：`cd src/backend && ./gradlew clean build`

## ライセンス
MIT License を採用しています。
