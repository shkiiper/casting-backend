ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS premium_since TIMESTAMP;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS premium_until TIMESTAMP;

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS premium_profile_price NUMERIC(19, 2) DEFAULT 1500;

ALTER TABLE subscription_plans
    ADD COLUMN IF NOT EXISTS premium_profile_days INTEGER DEFAULT 30;
