-- ==================================================================
-- V28: パフォーマンス最適化インデックス追加
-- ==================================================================
-- 目的: マルチテナントクエリの高速化（company_idフィルタリング）
-- 影響: 全主要マスターテーブルとトランザクションテーブル
-- 期待効果: クエリ実行時間 500ms → 10ms (98%改善)
-- ==================================================================

-- マスターテーブル: company_idインデックス
CREATE INDEX IF NOT EXISTS idx_employees_company_id
    ON m_employees(company_id);

CREATE INDEX IF NOT EXISTS idx_stores_company_id
    ON m_stores(company_id);

CREATE INDEX IF NOT EXISTS idx_employee_grades_company_id
    ON m_employee_grades(company_id);

CREATE INDEX IF NOT EXISTS idx_work_patterns_company_id
    ON m_work_patterns(company_id);

CREATE INDEX IF NOT EXISTS idx_store_distances_company_id
    ON m_store_distances(company_id);

CREATE INDEX IF NOT EXISTS idx_commute_methods_company_id
    ON m_commute_methods(company_id);

CREATE INDEX IF NOT EXISTS idx_business_trip_allowances_company_id
    ON m_business_trip_allowances(company_id);

-- 複合インデックス: よく使われるクエリパターン最適化
-- 従業員検索: 在職中/退職済みフィルタ
CREATE INDEX IF NOT EXISTS idx_employees_company_resignation
    ON m_employees(company_id, resignation_date);

-- displayIdによる検索最適化
CREATE INDEX IF NOT EXISTS idx_employees_company_display
    ON m_employees(company_id, display_id);

CREATE INDEX IF NOT EXISTS idx_stores_company_display
    ON m_stores(company_id, display_id);

CREATE INDEX IF NOT EXISTS idx_grades_company_display
    ON m_employee_grades(company_id, display_id);

-- ==================================================================
-- パフォーマンス検証用コメント
-- ==================================================================
-- 実行後、以下のコマンドでインデックス使用を確認:
-- EXPLAIN ANALYZE SELECT * FROM m_employees WHERE company_id = 1;
--
-- 期待結果:
-- - Seq Scan → Index Scan への変更
-- - 実行時間 500ms → 10ms 程度に短縮
-- ==================================================================
