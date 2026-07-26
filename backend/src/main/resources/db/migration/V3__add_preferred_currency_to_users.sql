ALTER TABLE users
    ADD COLUMN preferred_currency VARCHAR(3) NOT NULL DEFAULT 'AUD'
    CHECK (preferred_currency IN ('AUD', 'USD'));
