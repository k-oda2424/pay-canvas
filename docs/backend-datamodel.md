# payCanvas バックエンド データモデル概要

## 1. マスタ系
- `m_companies`: テナント会社情報。`status` や郵便番号・住所・電話・担当者情報を保持。
- `m_roles`: システム内の権限定義。`role_key` は `SUPER_ADMIN`/`COMPANY_ADMIN`/`STAFF`。
- `m_features`: 機能フラグ。`feature_key` で論理識別。
- `m_company_features`: 会社ごとの機能有効化設定。
- `m_employee_grades`: 等級マスタ。歩合率を保持。
- `m_salary_tiers`: 給与プラン。基本給や休日数を保持。
- `m_stores`: 店舗マスタ。店舗名・種別・住所を保持。
- `m_employees`: 従業員情報。等級／給与プラン／雇用区分／最低保証／残業等を保持。
- `m_users`: ログインユーザー。`password_hash` は BCrypt。
- `m_user_roles`: ユーザーとロールの多対多関係。

## 2. トランザクション・実績系
- `t_daily_attendances`: 日次勤怠実績。スタッフ・日付ごとに出退勤・ステータスを保持。
- `t_daily_store_metrics`: 店舗別日次売上。売上・値引・稼働時間。
- `t_daily_personal_metrics`: 個人別日次売上。技術／商品。
- `t_monthly_payrolls`: 月次給与明細。基本給・手当・控除・支給額・ステータス。
- `t_payroll_jobs`: 月次給与計算ジョブの履歴と進捗。
- `t_bonuses`: 将来拡張予定の賞与実績テーブル（未実装）。

## 3. モデル関連
- `UserAccount` ←→ `Company`/`UserRole`/`Role`
- `Employee` ←→ `Company`/`EmployeeGrade`/`SalaryTier`
- `CompanyFeature` ←→ `Company`/`Feature`

## 4. マイグレーション構成
- `V1__initial_schema.sql`: マスタ・勤怠・売上・給与ジョブ・サンプルデータ。
- `V2__auth_and_users.sql`: ロール／ユーザー／ロール紐付けとサンプルユーザー。
- `V3__store_master.sql`: 店舗マスタ作成・初期データ投入。
- `V4__refresh_tokens.sql`: リフレッシュトークンテーブルの追加。
- `V5__add_store_address.sql`: 店舗住所カラムの追加。
- `V6__extend_companies.sql`: 会社情報の住所・連絡先カラム追加。

## 5. 今後の拡張ポイント
- `t_bonuses` 等の未使用テーブルの実装。
- `audit_logs` や `job_executions` メタテーブルの追加。
- 認証トークンをJWT化し、`refresh_token` テーブルを導入するなどセキュリティ強化。
