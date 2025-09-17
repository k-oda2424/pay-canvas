# 美容室向けSaaS型給与計算システム プログラム設計書

## 1. バックエンド構成 (Spring Boot)

### 1.1 パッケージ構成
```
com.paycanvas.payroll
├── auth
│   ├── controller
│   ├── service
│   ├── dto
│   └── security
├── tenant
├── masterdata
├── dailymetrics
├── payroll
├── bonus
├── reporting
├── integration
├── common
└── config
```

### 1.2 主要クラスと役割
- `AuthController`: `/api/auth`系エンドポイント。`LoginRequest`を受け取り`JwtTokenResponse`を返却。
- `AuthService`: ユーザ認証、パスワード検証、JWT発行。
- `TenantController`: スーパー管理者向け会社/機能API。
- `TenantService`: `TenantRepository`を通じテナント情報を管理。機能フラグは`FeatureToggleService`で更新。
- `EmployeeController` (`/api/staff`): 従業員CRUD。入力DTOを`EmployeeAssembler`でエンティティへ変換。
- `DailyMetricsImportJob`: KING OF TIME APIクライアントを呼出し、勤怠データを`DailyAttendanceService`で保存。
- `PayrollExecutionService`: 月次計算を統括。`PayrollAggregator`、`AllowanceCalculator`、`CommissionCalculator`などのコンポーネントを呼び出し。
- `PayslipQueryService`: `t_monthly_payrolls`から給与明細DTOを構築。
- `BonusService`: 半期賞与の集計ロジックと支給予定生成を担当。

### 1.3 設定クラス
- `SecurityConfig`: Spring Security FilterChain、JWTフィルタ設定。
- `MultiTenantConfig`: Hibernate Filterの有効化、`TenantContextResolver`で`companyId`をThreadLocalに設定。
- `WebConfig`: CORS設定、メッセージコンバータ、LocaleResolver。
- `SchedulerConfig`: Quartz/Spring Scheduler設定。

---

## 2. DTO／エンティティ定義

### 2.1 認証関連
```java
class LoginRequest {
  String email;
  String password;
}

class JwtTokenResponse {
  String accessToken;
  String refreshToken;
  Instant expiresAt;
  UserSummary user;
}

class UserSummary {
  Long userId;
  Long companyId;
  String role; // SUPER_ADMIN, COMPANY_ADMIN, STAFF
  List<String> enabledFeatures;
}
```

### 2.2 従業員関連
```java
@Entity
@Table(name = "m_employees")
class Employee {
  @Id Long id;
  @ManyToOne Company company;
  @Column String name;
  @Enumerated(EnumType.STRING) EmploymentType employmentType;
  @ManyToOne EmployeeGrade grade;
  @ManyToOne SalaryTier salaryTier;
  Integer guaranteedMinimumSalary;
  Integer managerAllowance;
  Integer fixedOvertimeMinutes;
  BigDecimal commissionReductionRate;
}
```

### 2.3 月次給与
```java
@Entity
@Table(name = "t_monthly_payrolls")
class MonthlyPayroll {
  @EmbeddedId MonthlyPayrollId id; // employeeId + yearMonth
  @Column Integer baseSalary;
  @Column Integer allowanceTotal;
  @Column Integer deductionTotal;
  @Column Integer grossPay;
  @Column Integer netPay;
  @Column PayrollStatus status; // STAGING, CONFIRMED
  @Column Instant calculatedAt;
  @Column Instant confirmedAt;
  @Column Long confirmedBy;
  @OneToMany List<PayrollItem> items;
}
```

`PayrollItem`は給与明細行（区分、金額、メモ）を保持するコンポーネント。

---

## 3. API詳細設計

### 3.1 `POST /api/auth/login`
- **Request**
```json
{
  "email": "admin@example.com",
  "password": "Passw0rd!"
}
```
- **Response**
```json
{
  "accessToken": "jwt...",
  "refreshToken": "rjwt...",
  "expiresAt": "2024-01-01T00:15:00Z",
  "user": {
    "userId": 12,
    "companyId": 3,
    "role": "COMPANY_ADMIN",
    "enabledFeatures": ["BONUS_SYSTEM"]
  }
}
```

### 3.2 `GET /api/payroll/execute`
- **Query Parameters**: `year`, `month`
- **処理**:
  1. `PayrollExecutionService.execute(companyId, YearMonth)`を非同期実行。
  2. 即時レスポンスで`jobId`を返し、進捗は`GET /api/payroll/jobs/{jobId}`で取得。
- **Response**
```json
{
  "jobId": "2023-08-ACM-001",
  "status": "QUEUED"
}
```

### 3.3 `GET /api/payslips/{year}/{month}/{employeeId}`
- **認可**: スタッフ自身または会社管理者以上。
- **レスポンス項目**: `employeeName`, `baseSalary`, `allowances[]`, `deductions[]`, `grossPay`, `netPay`, `status`。

### 3.4 スーパー管理者機能
- `POST /api/super/companies`
  - Request: `name`, `status`, 初期管理者情報。
  - 処理: 会社マスタ作成、初期機能をデフォルト有効化、初期管理者ユーザを生成。

### 3.5 CSVエクスポート
- `GET /api/payroll/{year}/{month}/accounting-csv`
  - `Content-Type: text/csv`
  - 会計ソフト用フォーマットに整形。

---

## 4. サービス層ロジック

### 4.1 給与計算シーケンス
```
PayrollExecutionService.execute(companyId, yearMonth):
  validateInputs()
  ensureDailyMetricsClosed(companyId, yearMonth)
  employees = employeeRepository.findActiveByCompany(companyId)
  for each employee in parallel:
    metrics = metricsAggregator.collect(employee, yearMonth)
    base = salaryCalculator.calculateBase(employee, metrics)
    commission = commissionCalculator.calculate(employee, metrics)
    allowances = allowanceService.calculate(employee, metrics)
    deductions = deductionService.calculate(employee, metrics)
    gross = base + commission + allowances.total
    net = gross - deductions.total
    payrollRepository.saveStaging(employee, yearMonth, gross, net, detailItems)
  auditLogger.log("PAYROLL_EXECUTED", companyId, yearMonth)
  return jobResult
```

### 4.2 歩合計算
```
commissionCalculator:
  storeRevenuePerDay = storeMetricsRepo.aggregate(employee.storeIds, yearMonth)
  laborShare = storeRevenuePerDay / adjustedStaffCount
  personalShare = personalMetricsRepo.aggregate(employee.id, yearMonth)
  commissionBase = (laborShare * employee.commissionReductionRate) + personalShare
  commission = commissionBase * employee.grade.commissionRate
  return max(commission, employee.guaranteedMinimumSalary - baseSalary)
```

### 4.3 CSV生成
- テンプレート: `社員コード,氏名,支給額,控除額,振込額`。
- `CsvExportService`が`MonthlyPayroll`エンティティから`CsvRow`を生成し、`StringBuilder`で出力。

---

## 5. フロントエンド設計 (SPA)

### 5.1 ディレクトリ例
```
src/
├── api
│   ├── client.ts
│   └── payroll.ts
├── components
│   ├── Dashboard
│   ├── Payroll
│   ├── Staff
│   └── Shared
├── pages
│   ├── LoginPage.tsx
│   ├── DashboardPage.tsx
│   ├── PayrollExecutionPage.tsx
│   ├── PayslipListPage.tsx
│   └── StaffManagementPage.tsx
├── store
│   ├── authSlice.ts
│   ├── payrollSlice.ts
│   └── featureSlice.ts
└── utils
```

### 5.2 主要コンポーネント
- `PayrollExecutionPage`: 対象年月の選択、計算ジョブ開始、進捗表示。
- `PayslipList`: テーブル表示。スタッフロールは自身のみフィルタ。
- `FeatureTogglePanel`: スーパー管理者向け。`featureSlice`を利用しトグル変更。
- `DailyMetricsCalendar`: 勤怠・売上データをカレンダー表示し、モーダルで編集。

### 5.3 状態管理
- `authSlice`: `accessToken`, `user`, `tokenExpiry`。
- `payrollSlice`: 計算ジョブ一覧、選択中の給与明細。
- `dailyMetricsSlice`: 日次データ、読み込み状態。
- API呼出しは`createAsyncThunk`で実装。失敗時はグローバルエラートースト表示。

---

## 6. バッチ / ジョブ詳細

| ジョブ名 | スケジュール | クラス | 処理概要 |
| :--- | :--- | :--- | :--- |
| `dailyAttendanceImport` | 毎日05:00 | `DailyAttendanceImportJob` | KING OF TIME APIから前日分勤怠を取得しアップサート。 |
| `dailySalesImport` | 毎日05:30 | `DailySalesImportJob` | POS/CSVから売上データ読み込み。 |
| `monthlyPayrollFinalizeReminder` | 毎月25日09:00 | `PayrollReminderJob` | 未確定の給与がある会社管理者へメール通知。 |
| `semiAnnualBonusAggregation` | 7/1,1/1 03:00 | `BonusAggregationJob` | 半期賞与算出、明細作成。 |

ジョブ実行結果は`job_executions`テーブルに記録し、管理画面から参照可能。

---

## 7. エラーハンドリング・ログ
- `ApiError`レスポンスフォーマット `{ "code": "PAYROLL_VALIDATION_ERROR", "message": "...", "details": [...] }`
- 監査ログは`AuditLogger`インタフェースを通じて記録。
- 外部API失敗時は再試行ポリシー（指数バックオフ最大3回）。

---

## 8. テストケース例

| 層 | テストケース |
| :--- | :--- |
| Service | `PayrollExecutionService`が最低保証給を適用すること。 |
| Service | `CommissionCalculator`が歩合減免率を反映すること。 |
| Controller | `GET /api/payslips`がロール別にフィルタすること。 |
| Integration | 勤怠・売上データが月次計算に反映されること。 |
| Frontend | `PayrollExecutionPage`でジョブステータスがポーリング更新されること。 |

---

## 9. デプロイ・CI/CD補足
- GitHub Actionsで`main`マージ時に`make test`→`make build`→Dockerイメージ作成。
- ステージング環境へ自動デプロイ。本番は承認後に手動トリガー。
- FlywayでDBマイグレーション、テナントデータ初期化スクリプトは別管理。
