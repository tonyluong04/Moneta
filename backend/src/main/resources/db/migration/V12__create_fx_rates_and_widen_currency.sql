-- Exchange rates, one row per currency per day, always quoted against USD
-- (PRD §4.5.2). N rows a day, not N² pairs: a cross rate X→Y is derived as
-- rate(USD→Y) ÷ rate(USD→X) by CurrencyConverter.
--
-- Append only. Each day's set is new rows under its own rate_date and is never
-- overwritten, so Moneta builds its own rate history from the day this ships
-- (the free feed has no historical endpoint). Phase 6 snapshots convert at the
-- rates of their own date.
CREATE TABLE fx_rates (
    quote_currency VARCHAR(3) NOT NULL,

    -- the feed's own update date, in UTC — not the day we happened to fetch it
    rate_date DATE NOT NULL,

    -- units of quote_currency per 1 USD. 12 decimal places, because a rate
    -- rounded to 2 breaks any conversion involving VND or IDR (PRD §4.5.3).
    rate NUMERIC(24, 12) NOT NULL CHECK (rate > 0),

    fetched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (quote_currency, rate_date)
);

-- "newest set", "newest set on or before a date" and "every code in a set" all
-- filter on rate_date, which is not the leading column of the primary key
CREATE INDEX ix_fx_rates_rate_date ON fx_rates (rate_date);

-- Widen currency: a preferred currency is validated in code against the
-- configured display list (moneta.currencies.display, @DisplayCurrency), so
-- adding JPY is a config change, not a migration (PRD §4.5.4). Row currency
-- columns are already VARCHAR(3); which codes they accept is @SupportedCurrency.
ALTER TABLE users DROP CONSTRAINT users_preferred_currency_check;
