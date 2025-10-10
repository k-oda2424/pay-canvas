-- ==================================================================
-- V30: 月次給与計算テーブルの詳細化
-- ==================================================================
-- 目的: 給与明細の詳細項目を保存できるようテーブル構造を拡張
-- 対象: t_monthly_payrolls
-- ==================================================================

-- 既存カラムを削除して詳細カラムを追加
ALTER TABLE t_monthly_payrolls
    -- 支給項目の詳細
    ADD COLUMN position_allowance INTEGER DEFAULT 0,
    ADD COLUMN commission_amount INTEGER DEFAULT 0,
    ADD COLUMN overtime_pay INTEGER DEFAULT 0,
    ADD COLUMN review_incentive INTEGER DEFAULT 0,
    ADD COLUMN transportation_allowance INTEGER DEFAULT 0,
    ADD COLUMN other_allowances INTEGER DEFAULT 0,

    -- 控除項目の詳細
    ADD COLUMN health_insurance INTEGER DEFAULT 0,
    ADD COLUMN pension_insurance INTEGER DEFAULT 0,
    ADD COLUMN employment_insurance INTEGER DEFAULT 0,
    ADD COLUMN income_tax INTEGER DEFAULT 0,
    ADD COLUMN resident_tax INTEGER DEFAULT 0,
    ADD COLUMN other_deductions INTEGER DEFAULT 0,

    -- 勤怠情報
    ADD COLUMN work_days INTEGER DEFAULT 0,
    ADD COLUMN paid_leave_days DECIMAL(3,1) DEFAULT 0,
    ADD COLUMN absence_days DECIMAL(3,1) DEFAULT 0,
    ADD COLUMN overtime_hours DECIMAL(5,1) DEFAULT 0,

    -- 売上・歩合関連
    ADD COLUMN personal_sales_amount INTEGER DEFAULT 0,
    ADD COLUMN review_count INTEGER DEFAULT 0,

    -- 計算用情報
    ADD COLUMN calculation_note TEXT,
    ADD COLUMN standard_remuneration_grade INTEGER;

-- allowance_totalとdeduction_totalの計算は廃止し、個別項目から算出
-- 既存データがある場合の移行処理は不要（初期実装段階のため）

-- インデックス追加（検索高速化）
CREATE INDEX IF NOT EXISTS idx_monthly_payrolls_status
    ON t_monthly_payrolls(status);

CREATE INDEX IF NOT EXISTS idx_monthly_payrolls_target_month
    ON t_monthly_payrolls(target_year_month);

-- カラムコメント追加
COMMENT ON COLUMN t_monthly_payrolls.position_allowance IS '職務手当';
COMMENT ON COLUMN t_monthly_payrolls.commission_amount IS '歩合給';
COMMENT ON COLUMN t_monthly_payrolls.overtime_pay IS '時間外手当';
COMMENT ON COLUMN t_monthly_payrolls.review_incentive IS '口コミインセンティブ';
COMMENT ON COLUMN t_monthly_payrolls.transportation_allowance IS '交通費';
COMMENT ON COLUMN t_monthly_payrolls.other_allowances IS 'その他手当';
COMMENT ON COLUMN t_monthly_payrolls.health_insurance IS '健康保険料';
COMMENT ON COLUMN t_monthly_payrolls.pension_insurance IS '厚生年金保険料';
COMMENT ON COLUMN t_monthly_payrolls.employment_insurance IS '雇用保険料';
COMMENT ON COLUMN t_monthly_payrolls.income_tax IS '所得税';
COMMENT ON COLUMN t_monthly_payrolls.resident_tax IS '住民税';
COMMENT ON COLUMN t_monthly_payrolls.other_deductions IS 'その他控除';
COMMENT ON COLUMN t_monthly_payrolls.work_days IS '実働日数';
COMMENT ON COLUMN t_monthly_payrolls.paid_leave_days IS '有給休暇日数';
COMMENT ON COLUMN t_monthly_payrolls.absence_days IS '欠勤日数';
COMMENT ON COLUMN t_monthly_payrolls.overtime_hours IS '時間外労働時間';
COMMENT ON COLUMN t_monthly_payrolls.personal_sales_amount IS '個人売上額';
COMMENT ON COLUMN t_monthly_payrolls.review_count IS '口コミ件数';
COMMENT ON COLUMN t_monthly_payrolls.calculation_note IS '計算メモ（エラー、調整内容等）';
COMMENT ON COLUMN t_monthly_payrolls.standard_remuneration_grade IS '適用標準報酬等級';
COMMENT ON COLUMN t_monthly_payrolls.gross_pay IS '総支給額（base_salary + 各種手当の合計）';
COMMENT ON COLUMN t_monthly_payrolls.net_pay IS '差引支給額（gross_pay - 各種控除の合計）';
COMMENT ON COLUMN t_monthly_payrolls.status IS 'ステータス: DRAFT（計算中）/ CALCULATED（計算完了）/ CONFIRMED（確定）';

-- ==================================================================
-- 適用後の確認クエリ
-- ==================================================================
-- 確認: SELECT column_name, data_type, is_nullable, column_default
--       FROM information_schema.columns
--       WHERE table_name = 't_monthly_payrolls'
--       ORDER BY ordinal_position;
-- ==================================================================
