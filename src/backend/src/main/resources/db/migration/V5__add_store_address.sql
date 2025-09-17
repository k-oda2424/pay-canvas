ALTER TABLE m_stores ADD COLUMN IF NOT EXISTS address VARCHAR(255);

UPDATE m_stores SET address = '東京都渋谷区神宮前1-1-1' WHERE name = '表参道店' AND address IS NULL;
UPDATE m_stores SET address = '東京都中央区銀座2-2-2' WHERE name = '銀座店' AND address IS NULL;
UPDATE m_stores SET address = '東京都渋谷区宇田川町3-3-3' WHERE name = '渋谷店' AND address IS NULL;
