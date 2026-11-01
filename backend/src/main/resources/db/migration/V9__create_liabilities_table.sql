-- Things the user owes. Deliberately separate from assets: different categories,
-- a different value column, an interest rate, and no live pricing or cost basis.
-- Only the headline figure joins them — net worth = total assets - total liabilities.
CREATE TABLE liabilities (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,

    -- the 7 categories from PRD 4.3.5; labels and order live on LiabilityCategory
    category VARCHAR(30) NOT NULL CHECK (category IN (
        'MORTGAGE',
        'INVESTMENT_PROPERTY_LOAN',
        'CAR_LOAN',
        'PERSONAL_LOAN',
        'CREDIT_CARD',
        'STUDENT_DEBT',
        'OTHER_LIABILITY'
    )),

    currency VARCHAR(3) NOT NULL DEFAULT 'AUD',
    notes TEXT,

    -- Stored as a positive amount owing. The minus sign is a display concern,
    -- applied when the portfolio hero renders it and when net worth subtracts it.
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0 CHECK (balance >= 0),

    -- annual percentage, optional. 5,2 covers 0.00 to 999.99.
    interest_rate DECIMAL(5, 2) CHECK (interest_rate >= 0),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ix_liabilities_user_id ON liabilities (user_id);
