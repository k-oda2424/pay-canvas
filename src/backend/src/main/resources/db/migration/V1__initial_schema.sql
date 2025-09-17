CREATE TABLE IF NOT EXISTS m_companies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS m_features (
    id SERIAL PRIMARY KEY,
    feature_key VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS m_company_features (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES m_companies(id),
    feature_id INTEGER NOT NULL REFERENCES m_features(id),
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS m_employee_grades (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES m_companies(id),
    grade_name VARCHAR(100) NOT NULL,
    commission_rate NUMERIC(5,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS m_salary_tiers (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES m_companies(id),
    plan_name VARCHAR(100) NOT NULL,
    monthly_days_off INTEGER NOT NULL,
    base_salary INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS m_employees (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES m_companies(id),
    name VARCHAR(255) NOT NULL,
    grade_id INTEGER REFERENCES m_employee_grades(id),
    salary_tier_id INTEGER REFERENCES m_salary_tiers(id),
    employment_type VARCHAR(50) NOT NULL,
    store_name VARCHAR(255),
    guaranteed_minimum_salary INTEGER,
    manager_allowance INTEGER,
    fixed_overtime_minutes INTEGER,
    commission_reduction_rate NUMERIC(3,2)
);

CREATE TABLE IF NOT EXISTS t_monthly_payrolls (
    employee_id INTEGER NOT NULL REFERENCES m_employees(id),
    target_year_month CHAR(7) NOT NULL,
    base_salary INTEGER NOT NULL,
    allowance_total INTEGER NOT NULL,
    deduction_total INTEGER NOT NULL,
    gross_pay INTEGER NOT NULL,
    net_pay INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    calculated_at TIMESTAMP,
    confirmed_at TIMESTAMP,
    confirmed_by INTEGER,
    PRIMARY KEY (employee_id, target_year_month)
);

CREATE TABLE IF NOT EXISTS t_daily_attendances (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER NOT NULL REFERENCES m_employees(id),
    store_name VARCHAR(255) NOT NULL,
    work_date DATE NOT NULL,
    check_in TIME,
    check_out TIME,
    work_hours INTEGER,
    tardy_minutes INTEGER,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_daily_store_metrics (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES m_companies(id),
    store_name VARCHAR(255) NOT NULL,
    metric_date DATE NOT NULL,
    gross_sales INTEGER NOT NULL,
    discount_total INTEGER NOT NULL,
    total_hours INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS t_daily_personal_metrics (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER NOT NULL REFERENCES m_employees(id),
    metric_date DATE NOT NULL,
    service_sales INTEGER NOT NULL,
    product_sales INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS t_payroll_jobs (
    id SERIAL PRIMARY KEY,
    job_key VARCHAR(32) NOT NULL UNIQUE,
    target_month VARCHAR(16) NOT NULL,
    status VARCHAR(20) NOT NULL,
    progress INTEGER NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO m_companies (name, status)
VALUES ('payCanvas サンプル株式会社', 'ACTIVE')
ON CONFLICT DO NOTHING;

INSERT INTO m_features (feature_key, name, description)
VALUES
    ('BONUS_SYSTEM', '賞与管理', '半期賞与の集計と支給予約を管理します'),
    ('TRAVEL_ALLOWANCE', '出張手当管理', '出張経費や手当の申請・精算をサポートします')
ON CONFLICT DO NOTHING;

INSERT INTO m_company_features (company_id, feature_id, is_enabled)
SELECT c.id, f.id, CASE WHEN f.feature_key = 'BONUS_SYSTEM' THEN TRUE ELSE FALSE END
FROM m_companies c
JOIN m_features f ON TRUE
ON CONFLICT DO NOTHING;

-- 標準的な等級と給与テーブルのサンプルデータ
INSERT INTO m_employee_grades (company_id, grade_name, commission_rate)
SELECT c.id, 'S1', 0.45
FROM m_companies c
WHERE NOT EXISTS (
    SELECT 1 FROM m_employee_grades g WHERE g.company_id = c.id AND g.grade_name = 'S1'
);

INSERT INTO m_employee_grades (company_id, grade_name, commission_rate)
SELECT c.id, 'A2', 0.35
FROM m_companies c
WHERE NOT EXISTS (
    SELECT 1 FROM m_employee_grades g WHERE g.company_id = c.id AND g.grade_name = 'A2'
);

INSERT INTO m_employee_grades (company_id, grade_name, commission_rate)
SELECT c.id, 'B1', 0.25
FROM m_companies c
WHERE NOT EXISTS (
    SELECT 1 FROM m_employee_grades g WHERE g.company_id = c.id AND g.grade_name = 'B1'
);

INSERT INTO m_salary_tiers (company_id, plan_name, monthly_days_off, base_salary)
SELECT c.id, '週休2日プラン', 8, 280000
FROM m_companies c
WHERE NOT EXISTS (
    SELECT 1 FROM m_salary_tiers t WHERE t.company_id = c.id AND t.plan_name = '週休2日プラン'
);

INSERT INTO m_salary_tiers (company_id, plan_name, monthly_days_off, base_salary)
SELECT c.id, '週休3日プラン', 12, 220000
FROM m_companies c
WHERE NOT EXISTS (
    SELECT 1 FROM m_salary_tiers t WHERE t.company_id = c.id AND t.plan_name = '週休3日プラン'
);

-- サンプル従業員
INSERT INTO m_employees (
    company_id, name, grade_id, salary_tier_id, employment_type, store_name,
    guaranteed_minimum_salary, manager_allowance, fixed_overtime_minutes, commission_reduction_rate
)
SELECT c.id, '佐藤 花子', g.id, t.id, '正社員', '表参道店',
       300000, 30000, 1000, 1.00
FROM m_companies c
JOIN m_employee_grades g ON g.company_id = c.id AND g.grade_name = 'S1'
JOIN m_salary_tiers t ON t.company_id = c.id AND t.plan_name = '週休2日プラン'
WHERE NOT EXISTS (
    SELECT 1 FROM m_employees e WHERE e.company_id = c.id AND e.name = '佐藤 花子'
);

INSERT INTO m_employees (
    company_id, name, grade_id, salary_tier_id, employment_type, store_name,
    guaranteed_minimum_salary, manager_allowance, fixed_overtime_minutes, commission_reduction_rate
)
SELECT c.id, '田中 太郎', g.id, t.id, '正社員', '銀座店',
       260000, 20000, 600, 0.75
FROM m_companies c
JOIN m_employee_grades g ON g.company_id = c.id AND g.grade_name = 'A2'
JOIN m_salary_tiers t ON t.company_id = c.id AND t.plan_name = '週休2日プラン'
WHERE NOT EXISTS (
    SELECT 1 FROM m_employees e WHERE e.company_id = c.id AND e.name = '田中 太郎'
);

INSERT INTO m_employees (
    company_id, name, grade_id, salary_tier_id, employment_type, store_name,
    guaranteed_minimum_salary, manager_allowance, fixed_overtime_minutes, commission_reduction_rate
)
SELECT c.id, '鈴木 桃子', g.id, t.id, 'パート', '渋谷店',
       180000, 0, 0, 0.50
FROM m_companies c
JOIN m_employee_grades g ON g.company_id = c.id AND g.grade_name = 'B1'
JOIN m_salary_tiers t ON t.company_id = c.id AND t.plan_name = '週休3日プラン'
WHERE NOT EXISTS (
    SELECT 1 FROM m_employees e WHERE e.company_id = c.id AND e.name = '鈴木 桃子'
);

INSERT INTO t_monthly_payrolls (
    employee_id, target_year_month, base_salary, allowance_total, deduction_total,
    gross_pay, net_pay, status, calculated_at, confirmed_at, confirmed_by
)
SELECT e.id, '2024-03',
       COALESCE(t.base_salary, 0), 54000, 42000,
       COALESCE(t.base_salary, 0) + 54000, COALESCE(t.base_salary, 0) + 12000,
       'CONFIRMED', NOW(), NOW(), 1
FROM m_employees e
LEFT JOIN m_salary_tiers t ON t.id = e.salary_tier_id
WHERE NOT EXISTS (
    SELECT 1 FROM t_monthly_payrolls p WHERE p.employee_id = e.id AND p.target_year_month = '2024-03'
);

INSERT INTO t_daily_attendances (
    employee_id, store_name, work_date, check_in, check_out, work_hours, tardy_minutes, status
)
SELECT e.id,
       e.store_name,
       DATE '2024-04-10',
       TIME '09:30',
       TIME '19:00',
       8,
       CASE WHEN e.name = '佐藤 花子' THEN 10 ELSE 0 END,
       CASE WHEN e.name = '佐藤 花子' THEN '要確認' ELSE '承認済' END
FROM m_employees e
WHERE NOT EXISTS (
    SELECT 1 FROM t_daily_attendances a WHERE a.employee_id = e.id AND a.work_date = DATE '2024-04-10'
);

INSERT INTO t_daily_store_metrics (
    company_id, store_name, metric_date, gross_sales, discount_total, total_hours
)
SELECT c.id, m.store_name, DATE '2024-04-10',
       CASE m.store_name WHEN '表参道店' THEN 620000 WHEN '銀座店' THEN 550000 ELSE 380000 END,
       CASE m.store_name WHEN '表参道店' THEN 28000 WHEN '銀座店' THEN 18000 ELSE 12000 END,
       CASE m.store_name WHEN '表参道店' THEN 42 WHEN '銀座店' THEN 35 ELSE 28 END
FROM m_companies c
JOIN (SELECT DISTINCT store_name FROM m_employees WHERE store_name IS NOT NULL) m ON TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM t_daily_store_metrics s WHERE s.metric_date = DATE '2024-04-10' AND s.store_name = m.store_name
);

INSERT INTO t_daily_personal_metrics (
    employee_id, metric_date, service_sales, product_sales
)
SELECT e.id,
       DATE '2024-04-10',
       CASE e.name WHEN '佐藤 花子' THEN 180000 WHEN '田中 太郎' THEN 210000 ELSE 120000 END,
       CASE e.name WHEN '佐藤 花子' THEN 28000 WHEN '田中 太郎' THEN 32000 ELSE 18000 END
FROM m_employees e
WHERE NOT EXISTS (
    SELECT 1 FROM t_daily_personal_metrics p WHERE p.employee_id = e.id AND p.metric_date = DATE '2024-04-10'
);

INSERT INTO t_payroll_jobs (job_key, target_month, status, progress)
VALUES ('2024-03', '2024-03', 'COMPLETED', 100)
ON CONFLICT (job_key) DO NOTHING;
