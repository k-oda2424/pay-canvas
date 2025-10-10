-- V28: 従業員テーブルにメールアドレスフィールドを追加
-- 給与明細をメールで送信するため、従業員個別のメールアドレスが必要

ALTER TABLE m_employees
ADD COLUMN email VARCHAR(255);

COMMENT ON COLUMN m_employees.email IS '従業員のメールアドレス（給与明細送信用）';
