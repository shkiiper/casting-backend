ALTER TABLE users
    ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(255);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS city VARCHAR(255);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS description VARCHAR(255);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS telegram VARCHAR(255);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN;

UPDATE users
SET email_verified = false
WHERE email_verified IS NULL;

ALTER TABLE users
    ALTER COLUMN email_verified SET DEFAULT false;

ALTER TABLE users
    ALTER COLUMN email_verified SET NOT NULL;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS phone_verified BOOLEAN;

UPDATE users
SET phone_verified = false
WHERE phone_verified IS NULL;

ALTER TABLE users
    ALTER COLUMN phone_verified SET DEFAULT false;

ALTER TABLE users
    ALTER COLUMN phone_verified SET NOT NULL;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS active BOOLEAN;

UPDATE users
SET active = true
WHERE active IS NULL;

ALTER TABLE users
    ALTER COLUMN active SET DEFAULT true;

ALTER TABLE users
    ALTER COLUMN active SET NOT NULL;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS premium BOOLEAN;

UPDATE users
SET premium = false
WHERE premium IS NULL;

ALTER TABLE users
    ALTER COLUMN premium SET DEFAULT false;

ALTER TABLE users
    ALTER COLUMN premium SET NOT NULL;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS premium_until TIMESTAMP;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS customer_subscription_active BOOLEAN;

UPDATE users
SET customer_subscription_active = false
WHERE customer_subscription_active IS NULL;

ALTER TABLE users
    ALTER COLUMN customer_subscription_active SET DEFAULT false;

ALTER TABLE users
    ALTER COLUMN customer_subscription_active SET NOT NULL;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS customer_subscription_until TIMESTAMP;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

UPDATE users
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

ALTER TABLE users
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

UPDATE users
SET updated_at = CURRENT_TIMESTAMP
WHERE updated_at IS NULL;

ALTER TABLE users
    ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS first_name VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS last_name VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS city VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS main_photo_url VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS description VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS bio VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS gender VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS age INTEGER;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS ethnicity VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS experience_text VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS min_rate NUMERIC(19, 2);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS rate_unit VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS activity_type VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS location_name VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS address VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS rent_price NUMERIC(19, 2);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS rent_price_unit VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS extra_conditions VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS contact_email VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS contact_whatsapp VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS contact_telegram VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS social_links_json VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS published BOOLEAN;

UPDATE performer_profiles
SET published = false
WHERE published IS NULL;

ALTER TABLE performer_profiles
    ALTER COLUMN published SET DEFAULT false;

ALTER TABLE performer_profiles
    ALTER COLUMN published SET NOT NULL;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

UPDATE performer_profiles
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

ALTER TABLE performer_profiles
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

UPDATE performer_profiles
SET updated_at = CURRENT_TIMESTAMP
WHERE updated_at IS NULL;

ALTER TABLE performer_profiles
    ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS price_per_period NUMERIC(19, 2);

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS period_days INTEGER;

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS base_contact_limit INTEGER;

UPDATE subscription_plans
SET base_contact_limit = 40
WHERE base_contact_limit IS NULL;

ALTER TABLE subscription_plans
    ALTER COLUMN base_contact_limit SET DEFAULT 40;

ALTER TABLE subscription_plans
    ALTER COLUMN base_contact_limit SET NOT NULL;

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS booster_price NUMERIC(19, 2);

UPDATE subscription_plans
SET booster_price = 500
WHERE booster_price IS NULL;

ALTER TABLE subscription_plans
    ALTER COLUMN booster_price SET DEFAULT 500;

ALTER TABLE subscription_plans
    ALTER COLUMN booster_price SET NOT NULL;

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS booster_contacts INTEGER;

UPDATE subscription_plans
SET booster_contacts = 10
WHERE booster_contacts IS NULL;

ALTER TABLE subscription_plans
    ALTER COLUMN booster_contacts SET DEFAULT 10;

ALTER TABLE subscription_plans
    ALTER COLUMN booster_contacts SET NOT NULL;

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS casting_post_price NUMERIC(19, 2);

UPDATE subscription_plans
SET casting_post_price = 1000
WHERE casting_post_price IS NULL;

ALTER TABLE subscription_plans
    ALTER COLUMN casting_post_price SET DEFAULT 1000;

ALTER TABLE subscription_plans
    ALTER COLUMN casting_post_price SET NOT NULL;

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS casting_post_days INTEGER;

UPDATE subscription_plans
SET casting_post_days = 14
WHERE casting_post_days IS NULL;

ALTER TABLE subscription_plans
    ALTER COLUMN casting_post_days SET DEFAULT 14;

ALTER TABLE subscription_plans
    ALTER COLUMN casting_post_days SET NOT NULL;

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS active BOOLEAN;

UPDATE subscription_plans
SET active = true
WHERE active IS NULL;

ALTER TABLE subscription_plans
    ALTER COLUMN active SET DEFAULT true;

ALTER TABLE subscription_plans
    ALTER COLUMN active SET NOT NULL;

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

UPDATE payments
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

ALTER TABLE payments
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

UPDATE payments
SET updated_at = CURRENT_TIMESTAMP
WHERE updated_at IS NULL;

ALTER TABLE payments
    ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS customer_subscriptions (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    started_at TIMESTAMP,
    expires_at TIMESTAMP,
    total_contact_limit INTEGER NOT NULL DEFAULT 0,
    used_contacts INTEGER NOT NULL DEFAULT 0,
    booster_count INTEGER NOT NULL DEFAULT 0,
    payment_id VARCHAR(255),
    payment_status VARCHAR(255),
    paid_amount NUMERIC(19, 2),
    active BOOLEAN NOT NULL DEFAULT true
);

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS customer_id BIGINT;

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS plan_id BIGINT;

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS started_at TIMESTAMP;

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP;

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS total_contact_limit INTEGER;

UPDATE customer_subscriptions
SET total_contact_limit = 0
WHERE total_contact_limit IS NULL;

ALTER TABLE customer_subscriptions
    ALTER COLUMN total_contact_limit SET DEFAULT 0;

ALTER TABLE customer_subscriptions
    ALTER COLUMN total_contact_limit SET NOT NULL;

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS used_contacts INTEGER;

UPDATE customer_subscriptions
SET used_contacts = 0
WHERE used_contacts IS NULL;

ALTER TABLE customer_subscriptions
    ALTER COLUMN used_contacts SET DEFAULT 0;

ALTER TABLE customer_subscriptions
    ALTER COLUMN used_contacts SET NOT NULL;

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS booster_count INTEGER;

UPDATE customer_subscriptions
SET booster_count = 0
WHERE booster_count IS NULL;

ALTER TABLE customer_subscriptions
    ALTER COLUMN booster_count SET DEFAULT 0;

ALTER TABLE customer_subscriptions
    ALTER COLUMN booster_count SET NOT NULL;

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS payment_id VARCHAR(255);

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS payment_status VARCHAR(255);

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS paid_amount NUMERIC(19, 2);

ALTER TABLE customer_subscriptions
    ADD COLUMN IF NOT EXISTS active BOOLEAN;

UPDATE customer_subscriptions
SET active = true
WHERE active IS NULL;

ALTER TABLE customer_subscriptions
    ALTER COLUMN active SET DEFAULT true;

ALTER TABLE customer_subscriptions
    ALTER COLUMN active SET NOT NULL;

CREATE TABLE IF NOT EXISTS contact_views (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    viewed_at TIMESTAMP
);

ALTER TABLE contact_views
    ADD COLUMN IF NOT EXISTS customer_id BIGINT;

ALTER TABLE contact_views
    ADD COLUMN IF NOT EXISTS profile_id BIGINT;

ALTER TABLE contact_views
    ADD COLUMN IF NOT EXISTS viewed_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS uk_contact_views_customer_profile
    ON contact_views (customer_id, profile_id);

CREATE TABLE IF NOT EXISTS casting_posts (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    city VARCHAR(255),
    project_type VARCHAR(255),
    published_at TIMESTAMP,
    expires_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS customer_id BIGINT;

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS title VARCHAR(255);

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS description TEXT;

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS city VARCHAR(255);

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS project_type VARCHAR(255);

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS published_at TIMESTAMP;

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP;

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS active BOOLEAN;

UPDATE casting_posts
SET active = true
WHERE active IS NULL;

ALTER TABLE casting_posts
    ALTER COLUMN active SET DEFAULT true;

ALTER TABLE casting_posts
    ALTER COLUMN active SET NOT NULL;

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

ALTER TABLE casting_posts
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    token VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP
);

ALTER TABLE email_verification_tokens
    ADD COLUMN IF NOT EXISTS token VARCHAR(100);

ALTER TABLE email_verification_tokens
    ADD COLUMN IF NOT EXISTS user_id BIGINT;

ALTER TABLE email_verification_tokens
    ADD COLUMN IF NOT EXISTS code VARCHAR(6);

ALTER TABLE email_verification_tokens
    ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP;

ALTER TABLE email_verification_tokens
    ADD COLUMN IF NOT EXISTS used_at TIMESTAMP;

ALTER TABLE email_verification_tokens
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS idx_email_verification_token_token
    ON email_verification_tokens (token);

CREATE UNIQUE INDEX IF NOT EXISTS idx_email_verification_token_user
    ON email_verification_tokens (user_id);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    token VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP
);

ALTER TABLE password_reset_tokens
    ADD COLUMN IF NOT EXISTS token VARCHAR(100);

ALTER TABLE password_reset_tokens
    ADD COLUMN IF NOT EXISTS user_id BIGINT;

ALTER TABLE password_reset_tokens
    ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP;

ALTER TABLE password_reset_tokens
    ADD COLUMN IF NOT EXISTS used_at TIMESTAMP;

ALTER TABLE password_reset_tokens
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS idx_password_reset_token_token
    ON password_reset_tokens (token);

CREATE TABLE IF NOT EXISTS profile_photos (
    profile_id BIGINT NOT NULL,
    photo_url VARCHAR(255)
);

ALTER TABLE profile_photos
    ADD COLUMN IF NOT EXISTS profile_id BIGINT;

ALTER TABLE profile_photos
    ADD COLUMN IF NOT EXISTS photo_url VARCHAR(255);

CREATE TABLE IF NOT EXISTS profile_videos (
    profile_id BIGINT NOT NULL,
    video_url VARCHAR(255)
);

ALTER TABLE profile_videos
    ADD COLUMN IF NOT EXISTS profile_id BIGINT;

ALTER TABLE profile_videos
    ADD COLUMN IF NOT EXISTS video_url VARCHAR(255);

INSERT INTO users (
    email,
    password_hash,
    role,
    email_verified,
    phone_verified,
    active,
    banned,
    premium,
    customer_subscription_active,
    created_at,
    updated_at
)
VALUES (
    'admin@onset.com',
    '$2y$10$gnbOBHoVqP5JGHtgs5Dq4e469Z3HTslThNV3F7NogYvQvsWLWqpI2',
    'ADMIN',
    true,
    false,
    true,
    false,
    false,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    role = EXCLUDED.role,
    email_verified = EXCLUDED.email_verified,
    phone_verified = EXCLUDED.phone_verified,
    active = EXCLUDED.active,
    banned = EXCLUDED.banned,
    premium = EXCLUDED.premium,
    customer_subscription_active = EXCLUDED.customer_subscription_active,
    updated_at = CURRENT_TIMESTAMP;
