ALTER TABLE performer_profiles
    ADD COLUMN activity_types_json TEXT;

UPDATE performer_profiles
SET activity_types_json = to_json(
        ARRAY(
            SELECT value
            FROM unnest(regexp_split_to_array(activity_type, '\s*,\s*')) AS value
            WHERE BTRIM(value) <> ''
        )
    )::text
WHERE activity_type IS NOT NULL
  AND BTRIM(activity_type) <> ''
  AND activity_types_json IS NULL;
