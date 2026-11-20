-- The services now always set currency on create (the request's, else the user's
-- preferred currency). Dropping the column default means a row that somehow
-- skips that fails on NOT NULL instead of quietly becoming AUD.
-- users.preferred_currency keeps its AUD default: that one is a real default.
ALTER TABLE assets ALTER COLUMN currency DROP DEFAULT;
ALTER TABLE liabilities ALTER COLUMN currency DROP DEFAULT;
ALTER TABLE budget_entries ALTER COLUMN currency DROP DEFAULT;
