ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS height_cm INTEGER;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS weight_kg INTEGER;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS body_type VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS hair_color VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS eye_color VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS game_age_from INTEGER;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS game_age_to INTEGER;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS skills_json TEXT;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS intro_video_url TEXT;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS monologue_video_url TEXT;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS self_tape_video_url TEXT;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS experience_level VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS project_formats_json TEXT;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS achievements TEXT;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS floor INTEGER;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS location_type VARCHAR(255);

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS availability_from TIME;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS availability_to TIME;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS rental_terms TEXT;

ALTER TABLE performer_profiles
    ADD COLUMN IF NOT EXISTS contact_instagram VARCHAR(255);
