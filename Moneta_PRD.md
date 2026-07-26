# Product Requirements Document
## Moneta — Personal Finance & Wealth Management App

**Version:** 1.1
**Date:** July 2026
**Author:** Product Owner
**Status:** Draft

> **Changelog (v1.1):** Budgeting reworked from per-transaction logging to a **monthly budget-vs-actual** model (§4.2). No more individual transaction entries, recurring scheduler, or transaction history page. Data model, API, charts, and milestones updated to match. Tech stack and UI/UX guidelines retained from the implemented v1.0 baseline (Spring Boot 4, React 19, shadcn/ui, Tailwind v4, Recharts).

---

## 1. Overview

### 1.1 Product Summary

Moneta is a full-stack personal finance web application that gives users a unified view of their financial life — combining lightweight monthly budgeting with long-term wealth tracking. Users plan and track monthly budgets (budgeted vs actual), manage assets and liabilities across multiple categories, track a live stock portfolio, and visualise their financial health over time.

### 1.2 Goals

- Give individuals a single place to understand their complete financial picture
- Make budgeting **sustainable** with a lightweight monthly budget-vs-actual model (roughly 10 inputs per month, no per-transaction logging or bank feeds)
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
| **Standard User** | Can manage their own budgets, assets, and liabilities |
| **Admin** | Can manage users (future: v2 scope) |

---

## 3. Tech Stack

| Layer | Technology | Notes |
|-------|-----------|-------|
| Frontend | React 19 (TypeScript) + Vite | Desktop-first layout |
| CSS | Tailwind CSS v4 | Utility-first; integrated via `@tailwindcss/vite` plugin |
| UI Components | shadcn/ui | Radix UI primitives + `class-variance-authority` + `tailwind-merge` + `clsx`. Provides a `cn()` utility for conditional class merging. CSS-variable-based theming (see §8). |
| Form Handling | react-hook-form | Performant form state & validation |
| Notifications | sonner | Lightweight toast library |
| Animations | tw-animate-css | Tailwind-native animation utilities |
| Icons | lucide-react | Consistent, tree-shakeable icon set |
| Routing | react-router v7 | `createBrowserRouter` data-router pattern |
| Backend | Java 21 + Spring Boot 4 | REST API, Spring Security |
| Database | PostgreSQL 15+ | One schema per deployment |
| Auth | JWT (local) + OAuth2 (Google) | Spring Security + java-jwt |
| Stock Prices | Yahoo Finance API (via yfinance-compatible REST) or Alpha Vantage | Free tier acceptable for v1 |
| Crypto Prices | CoinGecko public API | Free, no key required |
| Charts | Recharts 2 | React-based composable charting library |
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

Rather than per-transaction logging (which requires bank feed integration to be sustainable), Moneta uses a **monthly budget overview** model. Users set their expected income and spending budgets once, then update actuals once a month — roughly 10 inputs per month rather than hundreds of individual entries.

#### 4.2.1 Budget Categories

Preset categories are seeded at startup. Users can add custom ones on top.

Income categories: Salary, Freelance, Investment Returns, Government Payments, Gift, Other Income

Expense categories: Housing, Groceries, Transport, Health, Dining Out, Entertainment, Subscriptions, Utilities, Insurance, Education, Shopping, Travel, Other Expense

Each category has a name, type (INCOME/EXPENSE), and optional colour.

#### 4.2.2 Monthly Budget Setup

Each month, the user configures:

- **Expected income** per income category (e.g. Salary: A$8,000/month)
- **Spending budget** per expense category (e.g. Groceries: A$600, Housing: A$2,000)

These are saved as the user's **budget plan** for that month. Budget plans can be copied forward from the previous month so users don't need to re-enter everything each time.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | |
| `user_id` | UUID FK | |
| `category_id` | UUID FK | |
| `month` | DATE | First day of the month, e.g. `2026-03-01` |
| `budgeted_amount` | DECIMAL(15,2) | What the user planned |
| `actual_amount` | DECIMAL(15,2) | What they actually spent/earned |
| `currency` | VARCHAR(3) | ISO 4217 |
| `notes` | TEXT | Optional |

#### 4.2.3 Actuals Entry

Once a month, the user fills in the **actual** amount for each category — what they actually earned and spent. This is a single form per month, not per transaction. Each row shows the category, the budgeted amount, and an input field for the actual.

#### 4.2.4 Dashboard — Budget Summary

The budget dashboard for the selected month shows:

- **Income card**: Total budgeted income vs total actual income
- **Expenses card**: Total budgeted expenses vs total actual expenses
- **Net savings card**: Actual income − actual expenses
- **Budget vs Actual bar chart**: Side-by-side bars (budgeted vs actual) per expense category, colour-coded — green if under budget, red if over
- **12-month savings trend line chart**: Net savings (income − expenses) plotted month by month

Month selector to navigate between months. Defaults to current month.

---

### 4.3 Assets & Liabilities Tracking

#### 4.3.1 Portfolio Overview

A full-width hero section (inspired by the NetWorth Navigator UI) showing:

- Total Assets value
- Total Liabilities value
- **Net Worth** = Assets − Liabilities
- Visual bar indicating Assets vs Liabilities ratio
- Asset allocation donut/pie chart (% per asset category)
- Net worth over time line chart (monthly snapshots, see 4.3.4)

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

#### 4.3.4 Stock Portfolio Detail Page

Accessed by clicking the **Shares / Stocks** card on the Portfolio overview. This is a dedicated page giving a full breakdown of all stock holdings.

**Page layout:**

- Page header: "Stock Portfolio" with total stock value and a **Refresh Prices** button
- Holdings grouped by **exchange** (e.g. US, HK, ASX), each group sorted by position size descending
- Each group has a **group header row** showing the exchange name on the left and the group's **% of total stock portfolio** on the right (e.g. `US  59.36%`)
- An **Add Stock** button to add a new holding

**Per-stock row layout (4 columns):**

| Column | Content |
|--------|---------|
| **Symbol / Name** | Ticker symbol in bold (large), company/holding name below in smaller grey text |
| **Price / Cost** | Live price on top; purchase cost per unit below in smaller grey text |
| **P/L** | Profit/loss percentage, colour-coded: green for positive (`+39.94%`), red for negative (`-38.13%`) |
| **Position** | This holding's % of total stock portfolio (e.g. `28.36%`) |

**P/L calculation:**
```
P/L % = ((live_price - purchase_price) / purchase_price) × 100
```

**Interactions:**
- Clicking a stock row opens an edit drawer/modal (update quantity, purchase price, or delete)
- Refresh Prices updates all live prices and recalculates P/L in real time
- Last price update timestamp shown in the page header

**Empty state:** "No stocks added yet. Add your first holding to start tracking."

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
| Budget vs Actual (bar) | Budget dashboard | Budgeted vs actual per expense category for selected month, colour-coded |
| 12-month Savings Trend (line) | Budget dashboard | Net savings (income − expenses) plotted month by month |
| Asset Allocation (donut) | Portfolio page | % of total assets per category |
| Net Worth Over Time (line) | Portfolio page | Monthly snapshot values: assets, liabilities, net worth |

All charts are interactive (hover tooltips). Currency values displayed in user's preferred currency (conversion rates fetched via a free FX API, e.g. ExchangeRate-API).

---

## 5. Data Model (Simplified ERD)

```
users
  id, email, password_hash, name, preferred_currency, created_at

categories
  id, user_id (null = preset), name, type (INCOME|EXPENSE), colour

budget_entries
  id, user_id, category_id, month, budgeted_amount, actual_amount, currency, notes

assets
  id, user_id, name, category (ENUM), currency, notes,
  manual_value, ticker_symbol, coin_id, quantity, purchase_price,
  last_live_price, last_price_updated_at, is_liability (BOOLEAN)

net_worth_snapshots
  id, user_id, snapshot_date, total_assets, total_liabilities, net_worth

refresh_tokens
  id, user_id, token_hash, expires_at, revoked
```

---

## 6. API Design (REST)

Base path: `/api/v1`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login, returns JWT |
| POST | `/auth/refresh` | Refresh access token |
| GET/PUT | `/users/me` | Get/update profile |
| GET/POST | `/budget` | List / create budget entries for a month |
| GET/PUT/DELETE | `/budget/{id}` | Get / update / delete a budget entry |
| POST | `/budget/copy-forward` | Copy previous month's budget plan to a new month |
| GET | `/budget/summary` | Aggregated budget vs actual summary for a month |
| GET/POST | `/categories` | List / create category |
| DELETE | `/categories/{id}` | Delete custom category |
| GET/POST | `/assets` | List / create asset or liability |
| GET/PUT/DELETE | `/assets/{id}` | Get / update / delete |
| POST | `/assets/refresh-prices` | Trigger live price refresh |
| GET | `/portfolio/summary` | Net worth, totals, allocation breakdown |
| GET/POST | `/snapshots` | List / create net worth snapshot |
| GET | `/reports/budget` | Budget report data (charts: budget vs actual, savings trend) |
| GET | `/reports/networth` | Net worth history for line chart |

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

- **Colour palette**: Teal primary (`#0D9488` range), white backgrounds, light grey cards — consistent with Figma Make prototype
- **Theming**: CSS custom properties (shadcn/ui convention). All colour tokens defined as CSS variables in a `theme.css` file (e.g. `--background`, `--foreground`, `--primary`, `--muted`, `--border`, etc.) and mapped to Tailwind via `@theme inline`. Supports future dark-mode toggle.
- **Typography**: System sans-serif stack (`system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif`)
- **Component library**: Use shadcn/ui primitives (`Button`, `Input`, `Label`, `Card`, `Select`, `Dialog`, `Tabs`, `Switch`, etc.) instead of raw HTML elements where a reusable primitive exists. All components live in `src/components/ui/`.
- **Utility function**: Use the `cn()` helper (from `clsx` + `tailwind-merge`) for conditional and merged Tailwind classes.
- **Layout**: Sidebar navigation with top-level sections: Portfolio, Budget, Stocks (matches Figma Make routes)
- **Desktop-first**: Min-width 1024px primary target; basic responsive layout for tablets
- **Empty states**: Friendly prompts ("No budget set for this month yet — copy last month or start fresh") rather than blank screens
- **Currency display**: Always show currency code alongside value (e.g. A$1,250 or USD 980)
- **Loading states**: Skeleton loaders for charts and live price refreshes
- **Notifications**: Use `sonner` toasts for success/error feedback on mutations
- **Forms**: Use `react-hook-form` for all multi-field forms (budget setup, actuals entry, assets, profile settings)

---

## 9. Milestones

| Phase | Scope | Target |
|-------|-------|--------|
| **Phase 1 — Foundation** | Monorepo structure, Docker Compose, PostgreSQL + Flyway migrations, Spring Security (JWT + Google OAuth2), protected React routes, user profile CRUD | Week 1–2 |
| **Phase 2 — Budgeting Core** | Budget entry model + migrations, preset category seeding + custom category CRUD, monthly budget setup (budgeted amounts per category), actuals entry form, copy-forward from previous month | Week 3–4 |
| **Phase 3 — Budget Dashboard** | Budget summary API (budget vs actual aggregation), budget vs actual bar chart, 12-month savings trend line chart, month selector | Week 5 |
| **Phase 4 — Portfolio (Manual)** | Asset/liability model + migrations, CRUD for manual asset categories (Cash, Property, Super, Vehicles, Other), portfolio overview page (net worth hero, category cards), asset allocation donut chart, Manage Assets/Liabilities UI | Week 6–7 |
| **Phase 5 — Live Pricing** | Stock price service (Yahoo Finance / Alpha Vantage), crypto price service (CoinGecko), 15-min backend price cache, Refresh Prices endpoint, Stock Portfolio detail page (grouped by exchange, P/L%, position%), fallback UI for failed fetches | Week 8 |
| **Phase 6 — Net Worth Tracking** | Net worth snapshot model, monthly scheduler, manual snapshot trigger, net worth over time line chart, FIRE toggle (exclude PPOR) | Week 9 |
| **Phase 7 — Multi-Currency** | FX rate service integration, currency conversion on aggregation queries, currency selector in user settings | Week 10 |
| **Phase 8 — Polish & Deploy** | Empty states, loading skeletons, error boundaries, CSV export for budget data, Docker production build, hosting setup (Railway / Render), basic monitoring | Week 11 |

---

## 10. Out of Scope for v1 — Future Considerations

- Bank feed / Open Banking integration
- Budget goals and alerts (e.g. "you've spent 80% of your dining budget")
- Mobile app (React Native)
- Admin dashboard for user management
- Social/sharing features
- Premium tier / Stripe payments
