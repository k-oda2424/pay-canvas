ALTER TABLE m_employees
    ADD COLUMN IF NOT EXISTS store_id INTEGER REFERENCES m_stores(id);

UPDATE m_employees e
SET store_id = s.id
FROM m_stores s
WHERE e.store_id IS NULL
  AND e.store_name IS NOT NULL
  AND s.company_id = e.company_id
  AND s.name = e.store_name;

ALTER TABLE m_employees
    DROP COLUMN IF EXISTS store_name;

CREATE INDEX IF NOT EXISTS idx_m_employees_store_id ON m_employees(store_id);
