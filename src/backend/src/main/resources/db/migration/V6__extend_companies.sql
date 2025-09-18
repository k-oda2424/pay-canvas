ALTER TABLE m_companies ADD COLUMN IF NOT EXISTS postal_code VARCHAR(16);
ALTER TABLE m_companies ADD COLUMN IF NOT EXISTS address VARCHAR(255);
ALTER TABLE m_companies ADD COLUMN IF NOT EXISTS phone VARCHAR(32);
ALTER TABLE m_companies ADD COLUMN IF NOT EXISTS contact_name VARCHAR(255);
ALTER TABLE m_companies ADD COLUMN IF NOT EXISTS contact_kana VARCHAR(255);
ALTER TABLE m_companies ADD COLUMN IF NOT EXISTS contact_email VARCHAR(255);

UPDATE m_companies
SET postal_code = '150-0001',
    address = '東京都渋谷区神宮前1-1-1',
    phone = '03-0000-0000',
    contact_name = '佐藤 花子',
    contact_kana = 'サトウ ハナコ',
    contact_email = 'admin@paycanvas.io'
WHERE name = 'payCanvas サンプル株式会社' AND (postal_code IS NULL OR postal_code = '');
