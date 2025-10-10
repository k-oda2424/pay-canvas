-- Flyway Migration: 標準報酬等級マスタテーブル作成

-- 既存の不完全なテーブルを削除
DROP TABLE IF EXISTS m_standard_monthly_remuneration CASCADE;

-- 新しい標準報酬等級マスタテーブル
CREATE TABLE m_standard_remuneration_grades (
    id SERIAL PRIMARY KEY,
    grade INTEGER NOT NULL,
    standard_amount INTEGER NOT NULL,
    salary_from INTEGER NOT NULL,
    salary_to INTEGER,
    health_insurance_no_care_employee INTEGER NOT NULL DEFAULT 0,
    health_insurance_no_care_employer INTEGER NOT NULL DEFAULT 0,
    health_insurance_with_care_employee INTEGER NOT NULL DEFAULT 0,
    health_insurance_with_care_employer INTEGER NOT NULL DEFAULT 0,
    pension_insurance_employee INTEGER NOT NULL DEFAULT 0,
    pension_insurance_employer INTEGER NOT NULL DEFAULT 0,
    effective_from DATE NOT NULL DEFAULT '2024-04-01',
    effective_to DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(grade, effective_from)
);

CREATE INDEX idx_standard_remuneration_grades_salary ON m_standard_remuneration_grades(salary_from, salary_to);
CREATE INDEX idx_standard_remuneration_grades_grade ON m_standard_remuneration_grades(grade);