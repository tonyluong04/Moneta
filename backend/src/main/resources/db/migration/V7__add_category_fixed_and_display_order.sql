-- Two additions to categories (PRD v1.2):
--
--   is_fixed       the amount is the same every month (rent, insurance, subscriptions).
--                  The actuals form pre-fills these from the budgeted amount so the user
--                  doesn't retype a number they already know.
--   display_order  the order categories appear in every form and chart. Seeded per type,
--                  so the UI never has to hardcode a sequence.
ALTER TABLE categories
    ADD COLUMN is_fixed BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0;

-- Income runs most-dependable first; expenses put the fixed commitments at the top,
-- then the variable categories, with the catch-all pinned last.
UPDATE categories c
SET display_order = v.display_order,
    is_fixed      = v.is_fixed
FROM (VALUES
    -- name,                type,       display_order, is_fixed
    ('Salary',              'INCOME',   1,  true),
    ('Government Payments', 'INCOME',   2,  false),
    ('Investment Returns',  'INCOME',   3,  false),
    ('Gift',                'INCOME',   4,  false),
    ('Freelance',           'INCOME',   5,  false),
    ('Other Income',        'INCOME',   6,  false),

    ('Housing',             'EXPENSE',  1,  true),
    ('Insurance',           'EXPENSE',  2,  true),
    ('Subscriptions',       'EXPENSE',  3,  true),
    ('Groceries',           'EXPENSE',  4,  false),
    ('Transport',           'EXPENSE',  5,  false),
    ('Health',              'EXPENSE',  6,  false),
    ('Dining Out',          'EXPENSE',  7,  false),
    ('Entertainment',       'EXPENSE',  8,  false),
    ('Utilities',           'EXPENSE',  9,  false),
    ('Education',           'EXPENSE', 10,  false),
    ('Shopping',            'EXPENSE', 11,  false),
    ('Travel',              'EXPENSE', 12,  false),
    ('Other Expense',       'EXPENSE', 13,  false)
) AS v(name, type, display_order, is_fixed)
WHERE c.user_id IS NULL
  AND c.name = v.name
  AND c.type = v.type;
