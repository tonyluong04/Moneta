CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    -- NULL user_id means this is a preset category, shared by every user.
    -- A non-NULL user_id means the category is custom and private to that user.
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    colour VARCHAR(7),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- A user cannot create two categories with the same name and type.
CREATE UNIQUE INDEX ux_categories_user_name_type
    ON categories (user_id, name, type)
    WHERE user_id IS NOT NULL;

-- Preset names must be unique among presets.
CREATE UNIQUE INDEX ux_categories_preset_name_type
    ON categories (name, type)
    WHERE user_id IS NULL;

CREATE INDEX ix_categories_user_id ON categories (user_id);

-- Preset categories (user_id NULL). Seeded once, visible to all users.
INSERT INTO categories (user_id, name, type, colour) VALUES
    (NULL, 'Salary',              'INCOME',  '#16a34a'),
    (NULL, 'Freelance',           'INCOME',  '#22c55e'),
    (NULL, 'Investment Returns',  'INCOME',  '#10b981'),
    (NULL, 'Government Payments', 'INCOME',  '#14b8a6'),
    (NULL, 'Gift',                'INCOME',  '#84cc16'),
    (NULL, 'Other Income',        'INCOME',  '#65a30d'),

    (NULL, 'Housing',             'EXPENSE', '#ef4444'),
    (NULL, 'Groceries',           'EXPENSE', '#f97316'),
    (NULL, 'Transport',           'EXPENSE', '#f59e0b'),
    (NULL, 'Health',              'EXPENSE', '#ec4899'),
    (NULL, 'Dining Out',          'EXPENSE', '#f43f5e'),
    (NULL, 'Entertainment',       'EXPENSE', '#a855f7'),
    (NULL, 'Subscriptions',       'EXPENSE', '#8b5cf6'),
    (NULL, 'Utilities',           'EXPENSE', '#0ea5e9'),
    (NULL, 'Insurance',           'EXPENSE', '#6366f1'),
    (NULL, 'Education',           'EXPENSE', '#3b82f6'),
    (NULL, 'Shopping',            'EXPENSE', '#d946ef'),
    (NULL, 'Travel',              'EXPENSE', '#06b6d4'),
    (NULL, 'Other Expense',       'EXPENSE', '#94a3b8');
