# 美容室向けSaaS型給与計算システム DB設計書

## 1. マスタテーブル (`m_`プレフィックス)

### `m_companies` (会社マスタ)
| 物理名 | データ型 | 説明 |
| :--- | :--- | :--- |
| `id` | `SERIAL` | **主キー (テナントID)** |
| `name` | `VARCHAR` | 会社名 |
| `status` | `VARCHAR` | 契約状況 ('ACTIVE', 'SUSPENDED') |

### `m_features` (機能マスタ)
| 物理名 | データ型 | 説明 |
| :--- | :--- | :--- |
| `id` | `SERIAL` | **主キー** |
| `feature_key`| `VARCHAR`| プログラム用キー (例: `BONUS_SYSTEM`) |
| `name` | `VARCHAR` | 機能名 (例: 「賞与管理機能」) |
| `description`| `TEXT` | 機能概要 |

### `m_company_features` (会社別機能設定)
| 物理名 | データ型 | 説明 |
| :--- | :--- | :--- |
| `id` | `SERIAL` | **主キー** |
| `company_id`| `INTEGER`| `m_companies`への**外部キー** |
| `feature_id`| `INTEGER`| `m_features`への**外部キー** |
| `is_enabled` | `BOOLEAN` | 有効フラグ |

### `m_employees` (従業員マスタ)
| 物理名 | データ型 | 説明 |
| :--- | :--- | :--- |
| `id` | `SERIAL` | **主キー** |
| `company_id`| `INTEGER`| `m_companies`への**外部キー** |
| `user_id` | `INTEGER` | (認証用テーブルへのFK) |
| `name` | `VARCHAR` | 氏名 |
| `grade_id`| `INTEGER`| `m_employee_grades`への**外部キー** |
| `salary_tier_id`|`INTEGER`| `m_salary_tiers`への**外部キー** |
| `employment_type` | `VARCHAR` | '正社員', 'パート' |
| `guaranteed_minimum_salary`| `INTEGER` | 最低保証給 |
| `manager_allowance` | `INTEGER` | 職責手当 |
| `fixed_overtime_minutes` | `INTEGER` | 固定残業時間(分) |
| `commission_reduction_rate`| `DECIMAL`| 歩合減免率 (1.0, 0.75, 0.5) |

### `m_employee_grades` (等級マスタ)
| 物理名 | データ型 | 説明 |
| :--- | :--- | :--- |
| `id` | `SERIAL` | **主キー** |
| `company_id`| `INTEGER`| `m_companies`への**外部キー** |
| `grade_name`| `VARCHAR`| 等級名 |
| `commission_rate`| `DECIMAL`| 歩合率 |

### `m_salary_tiers` (給与テーブル)
| 物理名 | データ型 | 説明 |
| :--- | :--- | :--- |
| `id` | `SERIAL` | **主キー** |
| `company_id`| `INTEGER`| `m_companies`への**外部キー** |
| `plan_name` | `VARCHAR` | 給与プラン名 (例: 「週休2日プラン」) |
| `monthly_days_off`| `INTEGER`| 月の休日数 |
| `base_salary`| `INTEGER`| 基本給 |

### `m_stores` (店舗マスタ)
| 物理名 | データ型 | 説明 |
| :--- | :--- | :--- |
| `id` | `SERIAL` | **主キー** |
| `company_id`| `INTEGER`| `m_companies`への**外部キー** |
| `name` | `VARCHAR` | 店舗名 |
| `store_type_id`| `INTEGER`| `m_store_types`への**外部キー** |

### `m_store_types` (店舗種類マスタ)
| 物理名 | データ型 | 説明 |
| :--- | :--- | :--- |
| `id` | `SERIAL` | **主キー** |
| `name` | `VARCHAR` | '指名制', 'チーム制' |

### その他マスタ
- `m_employee_commutes` (従業員通勤マスタ)
- `m_store_distances` (店舗間距離マスタ)

## 2. トランザクションテーブル (`t_`プレフィックス)

### `t_daily_attendances` (日次勤怠実績)
- 従業員ID, 店舗ID, 日付, 勤務種別, 出退勤時刻, 実働時間, 遅刻早退時間

### `t_daily_store_metrics` (日次店舗実績)
- 店舗ID, 日付, 総売上(値引前), 総値引額, 総稼働時間

### `t_daily_personal_metrics` (日次個人実績)
- 従業員ID, 日付, 個人売上, 商品販売売上

### `t_daily_expenses_and_deductions` (経費・控除実績)
- 従業員ID, 日付, 種別, 金額, 摘要

### `t_monthly_payrolls` (月次給与テーブル)
- 従業員ID, 対象年月, 基本給, 各種手当, 各種控除, 総支給額, 純支払額など給与明細項目

### `t_bonuses` (賞与実績テーブル)
- 従業員ID, 発生日, 賞与種別, 金額, 支払年月
