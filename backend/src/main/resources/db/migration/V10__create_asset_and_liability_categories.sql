-- Asset and liability categories become tables, so a user can add their own.
--
-- Shape mirrors `categories` (V4) exactly: a nullable user_id where NULL means a
-- preset shared by everyone, plus two partial unique indexes so presets are unique
-- among presets and a user's own names are unique to them. One mechanism for all
-- three category sets; the policy difference (budget categories stay read-only per
-- task 2.8, these do not) lives in the service layer where it belongs.

CREATE TABLE asset_categories (
    id SERIAL PRIMARY KEY,
    -- NULL user_id means this is a preset category, shared by every user.
    -- A non-NULL user_id means the category is custom and private to that user.
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    display_order INTEGER NOT NULL DEFAULT 0,
    -- Which price feed Phase 5 should use for assets in this category.
    -- NULL means valued by hand. A boolean would not be enough: 5.1 reads stocks
    -- from Yahoo and 5.2 reads crypto from CoinGecko, so the model has to say which.
    -- User-created categories are always NULL — nothing can price them.
    price_source VARCHAR(10) CHECK (price_source IN ('STOCK', 'CRYPTO')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_asset_categories_user_name
    ON asset_categories (user_id, name)
    WHERE user_id IS NOT NULL;

CREATE UNIQUE INDEX ux_asset_categories_preset_name
    ON asset_categories (name)
    WHERE user_id IS NULL;

CREATE INDEX ix_asset_categories_user_id ON asset_categories (user_id);

-- The eight presets (PRD 4.3.2), carrying the labels and descriptions that used to
-- live on the AssetCategory java enum.
INSERT INTO asset_categories (user_id, name, description, display_order, price_source) VALUES
    (NULL, 'Cash',                'Bank accounts, physical cash',     1, NULL),
    (NULL, 'Property - PPOR',     'Your primary home',                2, NULL),
    (NULL, 'Investment Property', 'Rental or investment properties',  3, NULL),
    (NULL, 'Shares',              'Stock market investments',         4, 'STOCK'),
    (NULL, 'Crypto',              'Digital currency assets',          5, 'CRYPTO'),
    (NULL, 'Super',               'Retirement savings accounts',      6, NULL),
    (NULL, 'Motor Vehicles',      'Cars, bikes, boats, etc.',         7, NULL),
    (NULL, 'Other assets',        'Valuables, collectibles',          8, NULL);

CREATE TABLE liability_categories (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    -- no description column: liabilities render as a plain list, not subtitled cards
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_liability_categories_user_name
    ON liability_categories (user_id, name)
    WHERE user_id IS NOT NULL;

CREATE UNIQUE INDEX ux_liability_categories_preset_name
    ON liability_categories (name)
    WHERE user_id IS NULL;

CREATE INDEX ix_liability_categories_user_id ON liability_categories (user_id);

-- The seven presets (PRD 4.3.5).
INSERT INTO liability_categories (user_id, name, display_order) VALUES
    (NULL, 'Mortgage (PPOR)',          1),
    (NULL, 'Investment Property Loan', 2),
    (NULL, 'Car Loan',                 3),
    (NULL, 'Personal Loan',            4),
    (NULL, 'Credit Card Debt',         5),
    (NULL, 'Student Debt (HECS/HELP)', 6),
    (NULL, 'Other Liability',          7);

-- Swap the V8/V9 enum columns for foreign keys.
--
-- Both tables are empty, so there is nothing to backfill and the NOT NULL can be
-- declared outright. If assets ever held rows at this point, this would need to be
-- three steps: add the column nullable, map each old enum name to its new id, then
-- set NOT NULL.
ALTER TABLE assets DROP COLUMN category;
ALTER TABLE assets
    ADD COLUMN asset_category_id INTEGER NOT NULL REFERENCES asset_categories(id);
CREATE INDEX ix_assets_asset_category_id ON assets (asset_category_id);

ALTER TABLE liabilities DROP COLUMN category;
ALTER TABLE liabilities
    ADD COLUMN liability_category_id INTEGER NOT NULL REFERENCES liability_categories(id);
CREATE INDEX ix_liabilities_liability_category_id
    ON liabilities (liability_category_id);
