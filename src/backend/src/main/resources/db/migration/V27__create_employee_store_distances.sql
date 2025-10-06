-- 従業員店舗距離マスタテーブル
-- 各従業員の自宅から各店舗への距離と通勤手段を管理

CREATE TABLE m_employee_store_distances (
  id SERIAL PRIMARY KEY,
  company_id INTEGER NOT NULL,
  employee_id INTEGER NOT NULL,
  store_id INTEGER NOT NULL,
  distance_km NUMERIC(5,1) NOT NULL,
  commute_method_id INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_employee_store_dist_company
    FOREIGN KEY (company_id) REFERENCES m_companies(id),

  CONSTRAINT fk_employee_store_dist_employee
    FOREIGN KEY (employee_id) REFERENCES m_employees(id) ON DELETE CASCADE,

  CONSTRAINT fk_employee_store_dist_store
    FOREIGN KEY (store_id) REFERENCES m_stores(id),

  CONSTRAINT fk_employee_store_dist_method
    FOREIGN KEY (commute_method_id) REFERENCES m_commute_methods(id),

  CONSTRAINT uk_employee_store_distances
    UNIQUE (employee_id, store_id)
);

-- インデックス
CREATE INDEX idx_employee_store_dist_company ON m_employee_store_distances(company_id);
CREATE INDEX idx_employee_store_dist_employee ON m_employee_store_distances(employee_id);
CREATE INDEX idx_employee_store_dist_store ON m_employee_store_distances(store_id);

-- コメント
COMMENT ON TABLE m_employee_store_distances IS '従業員店舗距離マスタ - 各従業員の自宅から各店舗への距離と通勤手段';
COMMENT ON COLUMN m_employee_store_distances.distance_km IS '自宅から店舗までの片道距離（km）';
COMMENT ON COLUMN m_employee_store_distances.commute_method_id IS 'この店舗への通勤手段';
