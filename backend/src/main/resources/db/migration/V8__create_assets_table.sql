-- Things the user owns. Liabilities live in their own table (V9) rather than here
-- with a sign flag: they share almost no columns with assets, so one table would
-- leave the six live-pricing columns below permanently null on every debt row.
CREATE TABLE assets (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,

    -- the 8 categories from PRD 4.3.2. labels, descriptions and card order live
    -- on the AssetCategory enum, not in this table.
    category VARCHAR(30) NOT NULL CHECK (category IN (
        'CASH',
        'PROPERTY_PPOR',
        'INVESTMENT_PROPERTY',
        'SHARES',
        'CRYPTO',
        'SUPERANNUATION',
        'MOTOR_VEHICLES',
        'OTHER_ASSETS'
    )),

    currency VARCHAR(3) NOT NULL DEFAULT 'AUD',
    notes TEXT,

    -- Nullable on purpose. From Phase 5 a share or coin is valued as
    -- quantity x last_live_price, and this stays null for those — which is how
    -- "priced by the API" stays distinguishable from "genuinely worth zero".
    manual_value DECIMAL(15, 2) CHECK (manual_value >= 0),

    -- Phase 5 (live pricing). Created now so the table and entity are not
    -- reopened later; nothing reads these until the price services exist.
    ticker_symbol VARCHAR(20),              -- Phase 5: e.g. VAS.AX, AAPL
    coin_id VARCHAR(50),                    -- Phase 5: CoinGecko id, e.g. bitcoin
    quantity DECIMAL(20, 8),                -- Phase 5: 8dp so crypto fractions survive
    purchase_price DECIMAL(15, 2),          -- Phase 5: cost basis, per unit
    last_live_price DECIMAL(15, 2),         -- Phase 5: last price fetched
    last_price_updated_at TIMESTAMP,        -- Phase 5: when that fetch happened

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Every read is "this user's assets", so index that.
CREATE INDEX ix_assets_user_id ON assets (user_id);
