ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS plan_id BIGINT;

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS booster_count INTEGER;

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS provider VARCHAR(32);

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS provider_payment_id VARCHAR(255);

UPDATE payments
SET provider = 'MOCK'
WHERE provider IS NULL;

ALTER TABLE payments
    ALTER COLUMN provider SET DEFAULT 'MOCK';

ALTER TABLE payments
    ALTER COLUMN provider SET NOT NULL;
