DO $$
DECLARE
    item RECORD;
BEGIN
    FOR item IN
        SELECT constraint_name
        FROM (
            SELECT
                c.conname AS constraint_name,
                array_agg(a.attname ORDER BY a.attname) AS columns
            FROM pg_constraint c
            JOIN unnest(c.conkey) AS k(attnum) ON true
            JOIN pg_attribute a
                ON a.attrelid = c.conrelid
               AND a.attnum = k.attnum
            WHERE c.conrelid = 'users'::regclass
              AND c.contype = 'u'
            GROUP BY c.conname
        ) constraints_to_check
        WHERE columns = ARRAY['email']::name[]
           OR columns = ARRAY['phone']::name[]
    LOOP
        EXECUTE format('ALTER TABLE users DROP CONSTRAINT IF EXISTS %I', item.constraint_name);
    END LOOP;

    FOR item IN
        SELECT idx.relname AS index_name
        FROM pg_index i
        JOIN pg_class idx ON idx.oid = i.indexrelid
        WHERE i.indrelid = 'users'::regclass
          AND i.indisunique
          AND NOT i.indisprimary
          AND i.indnkeyatts = 1
          AND pg_get_indexdef(i.indexrelid, 1, true) IN ('email', 'phone')
    LOOP
        EXECUTE format('DROP INDEX IF EXISTS %I', item.index_name);
    END LOOP;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_email_role
    ON users (email, role);
