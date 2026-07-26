CREATE TABLE budget_entries (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id INTEGER NOT NULL REFERENCES categories(id),
    -- Always the first day of the month this budget row is for, e.g. 2026-07-01.
    month DATE NOT NULL CHECK (EXTRACT(DAY FROM month) = 1),
    budgeted_amount DECIMAL(15, 2) NOT NULL DEFAULT 0 CHECK (budgeted_amount >= 0),
    actual_amount DECIMAL(15, 2) NOT NULL DEFAULT 0 CHECK (actual_amount >= 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'AUD',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- A user has exactly one budget row per category per month.
CREATE UNIQUE INDEX ux_budget_entries_user_category_month
    ON budget_entries (user_id, category_id, month);

-- The dashboard always loads "this user's rows for month X", so index that.
CREATE INDEX ix_budget_entries_user_month ON budget_entries (user_id, month);

CREATE INDEX ix_budget_entries_category_id ON budget_entries (category_id);