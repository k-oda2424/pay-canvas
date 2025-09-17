CREATE TABLE IF NOT EXISTS m_stores (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL REFERENCES m_companies(id),
    name VARCHAR(255) NOT NULL,
    store_type VARCHAR(100)
);

INSERT INTO m_stores (company_id, name, store_type)
SELECT c.id, v.name, v.store_type
FROM m_companies c
CROSS JOIN (VALUES
    ('表参道店', 'フラッグシップ'),
    ('銀座店', 'シティ'),
    ('渋谷店', 'サテライト')
) AS v(name, store_type)
WHERE NOT EXISTS (
    SELECT 1 FROM m_stores s WHERE s.company_id = c.id AND s.name = v.name
);
