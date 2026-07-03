ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_email_key;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS uk_users_email;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_phone_key;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS uk_users_phone;

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_email_role
    ON users (email, role);
