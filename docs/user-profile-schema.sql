-- Run before starting with ddl-auto=validate (the prod profile).
-- The default pgvector/dev profiles create this table automatically.
CREATE TABLE IF NOT EXISTS user_profile (
    user_id BIGINT PRIMARY KEY REFERENCES app_user(id) ON DELETE CASCADE,
    display_name VARCHAR(80),
    image_data BYTEA,
    image_updated_at TIMESTAMP(6) WITH TIME ZONE
);
