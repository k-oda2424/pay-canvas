CREATE TABLE IF NOT EXISTS m_roles (
    id SERIAL PRIMARY KEY,
    role_key VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS m_users (
    id SERIAL PRIMARY KEY,
    company_id INTEGER REFERENCES m_companies(id),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS m_user_roles (
    user_id INTEGER NOT NULL REFERENCES m_users(id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES m_roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO m_roles (role_key, name)
VALUES
    ('SUPER_ADMIN', 'スーパー管理者'),
    ('COMPANY_ADMIN', '会社管理者'),
    ('STAFF', '一般スタッフ')
ON CONFLICT (role_key) DO NOTHING;

INSERT INTO m_users (company_id, email, password_hash, display_name)
VALUES
    (NULL, 'super@paycanvas.io', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5zQ4jJWOweAV/QXp1eml8n1uX9g6OCW', 'payCanvas 運用担当'),
    (1, 'admin@paycanvas.io', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5zQ4jJWOweAV/QXp1eml8n1uX9g6OCW', '佐藤 花子'),
    (1, 'staff@paycanvas.io', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5zQ4jJWOweAV/QXp1eml8n1uX9g6OCW', '田中 太郎')
ON CONFLICT (email) DO NOTHING;

INSERT INTO m_user_roles (user_id, role_id)
SELECT u.id, r.id
FROM m_users u
JOIN m_roles r ON r.role_key = 'SUPER_ADMIN'
WHERE u.email = 'super@paycanvas.io'
ON CONFLICT DO NOTHING;

INSERT INTO m_user_roles (user_id, role_id)
SELECT u.id, r.id
FROM m_users u
JOIN m_roles r ON r.role_key = 'COMPANY_ADMIN'
WHERE u.email = 'admin@paycanvas.io'
ON CONFLICT DO NOTHING;

INSERT INTO m_user_roles (user_id, role_id)
SELECT u.id, r.id
FROM m_users u
JOIN m_roles r ON r.role_key = 'STAFF'
WHERE u.email = 'staff@paycanvas.io'
ON CONFLICT DO NOTHING;
