# Product Requirements Document
## NetWorth Tracker — Personal Finance & Wealth Management App

**Version:** 1.0  
**Date:** March 2026  
**Author:** Product Owner  
**Status:** Draft

---

## 1. Overview

### 1.1 Product Summary

NetWorth Tracker is a full-stack personal finance web application that gives users a unified view of their financial life — combining day-to-day budgeting with long-term wealth tracking. Users can log income and expenses, manage assets and liabilities across multiple categories, track a live stock portfolio, and visualise their financial health over time.

### 1.2 Goals

- Give individuals a single place to understand their complete financial picture
- Make budgeting frictionless with categorised transactions and recurring entry support
- Provide real-time portfolio valuation for stock and crypto holdings
- Support multiple users with isolated, secure data
- Be deployable as a low-cost hosted product usable by friends, family, and eventually a wider audience

### 1.3 Non-Goals (v1.0)

- Bank/Open Banking API integration (manual entry only)
- Native mobile app (desktop-first; responsive is a bonus)
- Tax reporting or financial advice
- Invoicing or business accounting features

---

## 2. Users

### 2.1 Target Audience

Financially aware individuals (primarily Australian users) who want more visibility over their money than a spreadsheet provides, but don't need the complexity of enterprise accounting software.

### 2.2 User Roles

| Role | Description |
|------|-------------|
| **Standard User** | Can manage their own transactions, assets, and liabilities |
| **Admin** | Can manage users (future: v2 scope) |

---

## 3. Tech Stack

| Layer | Technology | Notes |
|-------|-----------|-------|
| Frontend | React (TypeScript) | Vite recommended; desktop-first layout |
| Backend | Java 21 + Spring Boot 3 | REST API, Spring Security |
| Database | PostgreSQL 15+ | One schema per deployment |
| Auth | JWT (local) + OAuth2 (Google) | Spring Security + java-jwt |
| Stock Prices | Yahoo Finance API (via yfinance-compatible REST) or Alpha Vantage | Free tier acceptable for v1 |
| Crypto Prices | CoinGecko public API | Free, no key required |
| Charts | Recharts or Chart.js (React) | |
| Containerisation | Docker + Docker Compose | For local dev and deployment |
| Hosting | Railway / Render / Fly.io | Low-cost, suits personal project scale |

---

## 4. Feature Specifications

---

### 4.1 Authentication & User Management

#### Requirements

- Users register with email + password, or sign in via Google OAuth2
- Passwords stored hashed (BCrypt)
- JWT access token (15 min expiry) + refresh token (7 days, stored in HttpOnly cookie)
- Each user's data is fully isolated at the database level (all tables include `user_id` foreign key)
- User profile: name, email, preferred currency (default AUD), avatar (optional)

#### Screens

- Register / Login page
- Profile settings page (change name, currency preference, password)

---

### 4.2 Budgeting

#### 4.2.1 Transactions

Users can log income and expense transactions manually.

**Transaction fields:**

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | |
| `user_id` | UUID FK | |
| `type` | ENUM | `INCOME` or `EXPENSE` |
| `amount` | DECIMAL(15,2) | |
| `currency` | VARCHAR(3) | ISO 4217, e.g. AUD, USD |
| `category_id` | UUID FK | |
| `description` | TEXT | Optional |
| `date` | DATE | User-specified |
| `is_recurring` | BOOLEAN | |
| `recurrence_rule` | VARCHAR | `DAILY`, `WEEKLY`, `FORTNIGHTLY`, `MONTHLY`, `ANNUALLY` |
| `recurrence_end_date` | DATE | Optional; null = indefinite |
| `created_at` | TIMESTAMP | |

**Preset categories (seeded at startup):**

Income: Salary, Freelance, Investment Returns, Government Payments, Gift, Other Income  
Expense: Housing, Groceries, Transport, Health, Dining Out, Entertainment, Subscriptions, Utilities, Insurance, Education, Shopping, Travel, Other Expense

Users can add custom categories on top of presets. Categories have a name, type (INCOME/EXPENSE), and optional colour/icon.

**Recurring transactions:**
- When a recurring transaction is saved, the system generates future transaction instances up to 12 months ahead (scheduled job runs nightly)
- User can edit/delete a single instance or "this and all future"

#### 4.2.2 Transaction History

- Paginated list view (20 per page), newest first
- Filter by: date range, type (income/expense), category, amount range
- Search by description
- Inline edit and delete
- Display running balance

#### 4.2.3 Dashboard — Budget Summary

> **UI Inspiration:** See [`UI inspiration/FinPlanner_dashboard.png`](UI%20inspiration/FinPlanner_dashboard.png) — sidebar navigation on the left, main content area with card-based layout showing account summaries, upcoming transactions, and an income vs expense bar chart.

A top-level budget dashboard showing:

- **Balance card**: Total income − total expenses for the selected period (default: current month)
- **Income card**: Sum of income transactions for the period
- **Expense card**: Sum of expense transactions for the period
- **Monthly income vs expense bar chart**: 12-month rolling view, grouped by month, income bar vs expense bar side by side
- **Spending by category donut chart**: Top categories by spend for the selected period
- **Recent transactions list**: Last 10 entries with quick-add shortcut

Period selector: This Week / This Month / This Quarter / This Year / Custom Range

---

### 4.3 Assets & Liabilities Tracking

#### 4.3.1 Portfolio Overview

> **UI Inspiration:** See [`UI inspiration/NetWorth_Navigator_portfolio.png`](UI%20inspiration/NetWorth_Navigator_portfolio.png) — full-width teal gradient hero banner with total Assets, Liabilities, and Net Worth displayed alongside horizontal progress bars showing the ratio. Below the hero, a row of category cards (Cash, Property – PPOR, Investment Property, Shares, Crypto, Super, Motor Vehicles, Other Assets) each showing a mini donut/ring chart, percentage of total, category name, description, and value in the user's preferred currency.

A full-width hero section showing:

- Total Assets value
- Total Liabilities value
- **Net Worth** = Assets − Liabilities
- Visual bar indicating Assets vs Liabilities ratio
- Asset allocation donut/pie chart (% per asset category)
- Net worth over time line chart (monthly snapshots, see 4.3.5)

Below the hero, a horizontally scrollable row of **asset category cards**, one per category:

| Card Element | Details |
|--------------|---------|
| Mini ring chart | Filled proportionally to % of total assets |
| Percentage label | e.g. "32%" |
| Category name | e.g. "Shares" |
| Description | Short subtitle, e.g. "Stock market investments" |
| Total value | In user's preferred currency, e.g. "A$45,200" |

#### 4.3.2 Asset Categories

| Category | Sub-type | Notes |
|----------|----------|-------|
| Cash | Bank account, Term deposit, Physical cash | Manual value |
| Property – PPOR | Primary residence | Manual value; toggle to exclude from FIRE calc |
| Investment Property | Rental/investment real estate | Manual value |
| Shares / Stocks | ASX, NYSE, NASDAQ tickers | Ticker + quantity → live price |
| Crypto | BTC, ETH, etc. | Coin ID → live price via CoinGecko |
| Superannuation | Retirement savings | Manual value |
| Motor Vehicles | Car, bike, boat, etc. | Manual value |
| Other Assets | Valuables, collectibles, etc. | Manual value |

Each asset card in the UI shows: category name, total value in preferred currency, percentage of total assets.

#### 4.3.3 Adding / Editing an Asset

> **UI Inspiration:** See [`UI inspiration/Manage_Assets_panel.png`](UI%20inspiration/Manage_Assets_panel.png) — clean card-based form with fields for Asset Name, Asset Type (dropdown), and Value. A prominent teal "Add Asset" button, a "Refresh Live Prices" button in the card header, the FIRE exclusion toggle above the form, and an "Actions & Exports" section below with "Download Snapshot" and "Download Statement" buttons.

**Common fields for all assets:**

| Field | Notes |
|-------|-------|
| Name | e.g. "ANZ Savings", "VDHG Holdings" |
| Category | Dropdown (see 4.3.2) |
| Currency | ISO code |
| Notes | Optional |

**Shares (additional fields):**

| Field | Notes |
|-------|-------|
| Ticker Symbol | e.g. `VAS.AX`, `AAPL` |
| Quantity (units held) | Number of shares |
| Purchase Price (per unit) | For cost basis / gain/loss display |
| Exchange | ASX, NYSE, etc. |

Price is fetched live from stock API; value = quantity × live price. Users can also override with a manual price if API lookup fails.

**Crypto (additional fields):**

| Field | Notes |
|-------|-------|
| Coin ID | e.g. `bitcoin`, `ethereum` (CoinGecko ID) |
| Quantity | Amount held |
| Purchase Price | For cost basis |

#### 4.3.4 Stock Portfolio View

> **UI Inspiration:** See [`UI inspiration/Stock_Portfolio_view.png`](UI%20inspiration/Stock_Portfolio_view.png) — holdings grouped by exchange/market with clean typographic hierarchy, colour-coded P/L percentages, and per-stock position weight.

The stock portfolio is displayed as a **grouped list**, organised by exchange/market. This view is accessible from the Portfolio page when the user drills into the "Shares" category card or via a dedicated "Stock Portfolio" tab.

**Group header (one per exchange):**

| Element | Details |
|---------|---------|
| Exchange label | e.g. "US", "HK", "ASX" — left-aligned, bold |
| Group weight | Percentage of total stock portfolio held in this exchange, displayed top-right of the group header (e.g. "59.36%") |

**Per-stock row within each group:**

| Column | Details |
|--------|---------|
| **Symbol** | Ticker symbol in large, bold text (e.g. `FUTU`, `AAPL`, `TSLA`) |
| **Company name** | Displayed below the symbol in smaller, grey/muted text (e.g. "Futu [moomoo]", "Apple", "Tesla") |
| **Price / Cost** | Current live price on top in standard weight (e.g. `55.980`); purchase price (cost basis) below in smaller grey text (e.g. `40.002`) |
| **P/L %** | Profit/loss percentage, **colour-coded**: green for positive (e.g. `+39.94%`), red for negative (e.g. `-38.13%`) |
| **Positions %** | That stock's share of the total stock portfolio value (e.g. `28.36%`, `18.46%`) |

**Behaviour notes:**
- Rows are sorted by position weight (largest first) within each exchange group
- Exchange groups are sorted by total group weight (largest first)
- Tapping/clicking a row expands to show additional details: quantity held, total market value, total cost basis, and absolute P/L in currency
- A "Refresh Live Prices" button at the top triggers a batch price update for all tickers
- Last-updated timestamp shown below the button (e.g. "Prices as of 27 Mar 2026, 2:35 PM")

#### 4.3.5 Liabilities

Same structure as assets, with these categories:

- Mortgage (PPOR)
- Investment Property Loan
- Car Loan
- Personal Loan
- Credit Card Debt
- Student Debt (HECS/HELP)
- Other Liability

Fields: name, category, balance owing, interest rate (optional), currency.

#### 4.3.6 Net Worth Snapshots

- A scheduled job runs on the 1st of each month, saving a snapshot of total assets, liabilities, and net worth per user
- Users can also manually trigger a snapshot ("Save Snapshot")
- Snapshots power the Net Worth Over Time line chart

#### 4.3.7 Live Price Refresh

- "Refresh Live Prices" button on the manage screen triggers a batch API call for all stock tickers and crypto coins held by the user
- Backend caches prices for 15 minutes to avoid rate limits
- Display last-updated timestamp next to each live-priced asset
- Graceful fallback: if API call fails, show last known price with a warning indicator

#### 4.3.8 FIRE Toggle

- Toggle on the portfolio page: "Exclude PPOR from totals (for FIRE calculation)"
- When enabled, the PPOR asset value is excluded from the net worth figure and charts
- Preference is saved per user

---

### 4.4 Reports & Charts

| Chart | Location | Description |
|-------|----------|-------------|
| Monthly Income vs Expense (bar) | Budget dashboard | 12-month rolling, income + expense bars per month |
| Spending by Category (donut) | Budget dashboard | Top expense categories, current period |
| Asset Allocation (donut) | Portfolio page | % of total assets per category |
| Net Worth Over Time (line) | Portfolio page | Monthly snapshot values: assets, liabilities, net worth |

All charts are interactive (hover tooltips). Currency values displayed in user's preferred currency (conversion rates fetched via a free FX API, e.g. ExchangeRate-API).

---

## 5. Data Model (Simplified ERD)

```
users
  id, email, password_hash, name, preferred_currency, created_at

categories
  id, user_id (null = preset), name, type (INCOME|EXPENSE), colour, icon

transactions
  id, user_id, type, amount, currency, category_id, description,
  date, is_recurring, recurrence_rule, recurrence_end_date, parent_transaction_id

assets
  id, user_id, name, category (ENUM), currency, notes,
  manual_value, ticker_symbol, coin_id, quantity, purchase_price,
  exchange, company_name,
  last_live_price, last_price_updated_at, is_liability (BOOLEAN)

net_worth_snapshots
  id, user_id, snapshot_date, total_assets, total_liabilities, net_worth

refresh_tokens
  id, user_id, token_hash, expires_at, revoked
```

> **Note:** The `exchange` field on assets enables grouping stocks by market in the portfolio view. The `company_name` field stores the display name shown beneath the ticker symbol (can be auto-populated from the stock API or manually entered).

---

## 6. API Design (REST)

Base path: `/api/v1`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login, returns JWT |
| POST | `/auth/refresh` | Refresh access token |
| GET/PUT | `/users/me` | Get/update profile |
| GET/POST | `/transactions` | List / create transaction |
| GET/PUT/DELETE | `/transactions/{id}` | Get / update / delete |
| GET | `/transactions/summary` | Aggregated budget summary for period |
| GET/POST | `/categories` | List / create category |
| DELETE | `/categories/{id}` | Delete custom category |
| GET/POST | `/assets` | List / create asset or liability |
| GET/PUT/DELETE | `/assets/{id}` | Get / update / delete |
| POST | `/assets/refresh-prices` | Trigger live price refresh |
| GET | `/portfolio/summary` | Net worth, totals, allocation breakdown |
| GET | `/portfolio/stocks` | Stock holdings grouped by exchange with P/L and position weights |
| GET/POST | `/snapshots` | List / create net worth snapshot |
| GET | `/reports/budget` | Budget report data (charts) |
| GET | `/reports/networth` | Net worth history for line chart |

### 6.1 Stock Portfolio Response Shape

`GET /api/v1/portfolio/stocks`

```json
{
  "totalStockValue": 125000.00,
  "currency": "AUD",
  "lastUpdatedAt": "2026-03-27T14:35:00Z",
  "exchangeGroups": [
    {
      "exchange": "US",
      "groupWeight": 59.36,
      "holdings": [
        {
          "id": "uuid",
          "tickerSymbol": "FUTU",
          "companyName": "Futu [moomoo]",
          "currentPrice": 55.980,
          "purchasePrice": 40.002,
          "currency": "USD",
          "quantity": 100,
          "marketValue": 5598.00,
          "costBasis": 4000.20,
          "profitLossPercent": 39.94,
          "positionWeight": 28.36
        }
      ]
    }
  ]
}
```

---

## 7. Non-Functional Requirements

| Requirement | Target |
|-------------|--------|
| API response time (p95) | < 500ms for standard reads |
| Live price refresh | < 3s for up to 20 tickers |
| Auth token security | HttpOnly cookies for refresh token; short-lived JWT |
| Data isolation | All queries must be scoped to `user_id`; enforced at service layer |
| Input validation | Bean Validation (Jakarta) on all DTOs |
| Error handling | Global `@ControllerAdvice`; structured error responses |
| CORS | Restrict to frontend origin in production |
| Rate limiting | Apply to `/assets/refresh-prices` and auth endpoints |

---

## 8. UI/UX Guidelines

- **Colour palette**: Teal primary (`#0D9488` range), white backgrounds, light grey cards — consistent with inspiration screenshots
- **Typography**: Clean sans-serif (Inter or similar)
- **Layout**: Sidebar navigation with top-level sections: Dashboard, Transactions, Portfolio, Reports, Settings
- **Desktop-first**: Min-width 1024px primary target; basic responsive layout for tablets
- **Empty states**: Friendly prompts ("No transactions yet — add your first one") rather than blank screens
- **Currency display**: Always show currency code alongside value (e.g. A$1,250 or USD 980)
- **Loading states**: Skeleton loaders for charts and live price refreshes
- **Colour-coded P/L**: Green (`#16A34A`) for positive profit/loss values, red (`#DC2626`) for negative — used consistently across the stock portfolio view and any gain/loss indicators
- **Typographic hierarchy in data rows**: Primary identifiers (ticker symbols, asset names) in bold/semibold; secondary info (company names, cost basis) in smaller muted grey text

---

## 9. Milestones

| Phase | Scope | Target |
|-------|-------|--------|
| **Phase 1 — Foundation** | Auth (JWT + Google OAuth), user profile, DB schema, CI/CD pipeline | Week 1–2 |
| **Phase 2 — Budgeting** | Transactions CRUD, categories, recurring logic, budget dashboard + charts | Week 3–5 |
| **Phase 3 — Portfolio** | Assets/liabilities CRUD, live price integration (stocks + crypto), portfolio overview + stock portfolio grouped view + charts | Week 6–8 |
| **Phase 4 — Polish** | Reports page, net worth snapshots, multi-currency FX, FIRE toggle, export (CSV) | Week 9–10 |
| **Phase 5 — Deploy** | Docker, environment config, hosting setup, basic monitoring | Week 11 |

---

## 10. Out of Scope for v1 — Future Considerations

- Bank feed / Open Banking integration
- Budget goals and alerts (e.g. "you've spent 80% of your dining budget")
- Mobile app (React Native)
- Admin dashboard for user management
- Social/sharing features
- Premium tier / Stripe payments
