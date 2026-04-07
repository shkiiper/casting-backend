CREATE TABLE IF NOT EXISTS creator_portfolio_photos (
    profile_id BIGINT NOT NULL,
    photo_url VARCHAR(255)
);

ALTER TABLE creator_portfolio_photos
    ADD COLUMN IF NOT EXISTS profile_id BIGINT;

ALTER TABLE creator_portfolio_photos
    ADD COLUMN IF NOT EXISTS photo_url VARCHAR(255);
