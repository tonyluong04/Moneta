### Phase 1 — Foundation

**Goal:** Project scaffold, dev environment, auth system, login UI.

> **Coding conventions (all phases):** Backend — plain Java only, no Lombok. Write explicit getters, setters, and constructors instead. Frontend — use the browser's built-in `fetch` API instead of Axios. Prefer readable, explicit code over shortcuts.

| # | Task | Status |
|---|------|--------|
| 1.1 | **Frontend scaffold** — Vite + React 19 + TypeScript, Tailwind CSS v4 (`@tailwindcss/vite`), `@` path alias in vite.config.ts + tsconfig.app.json | ✅ Done |
| 1.2 | **shadcn/ui foundation** — Install `clsx`, `tailwind-merge`, `class-variance-authority`, `tw-animate-css`. Create `cn()` utility (`src/lib/utils.ts`). Add base components: `Button`, `Input`, `Label`, `Card` in `src/components/ui/` | ✅ Done |
| 1.3 | **CSS variable theming** — Create `src/styles/theme.css` with shadcn/ui convention (light + dark tokens), `@theme inline` mapping, base typography. Import in index.css | ✅ Done |
| 1.4 | **Routing setup** — `react-router v7` with `createBrowserRouter` in App.tsx | ✅ Done |
| 1.5 | **Login / Register page** — `AuthForm` component with login/register toggle, fields, show/hide password, error display, demo login button. Uses `Button`, `Input`, `Label` | ✅ Done |
| 1.6 | **Frontend tooling** — Install `react-hook-form`, `sonner`, Radix UI primitives. VS Code settings for Tailwind at-rule warnings | ✅ Done |
| 1.7 | **Backend scaffold** — Spring Boot 4 + Java 21, Maven. Dependencies: web, security, data-jpa, validation, PostgreSQL, DevTools (no Lombok) | ✅ Done |
| 1.8 | **Docker Compose** — PostgreSQL 16 container on port 5433, application.properties configured | ✅ Done |
| 1.9 | **Flyway migrations** — Add Flyway dependency, `V1__create_users_table.sql`, `V2__create_refresh_tokens_table.sql`. Replace `ddl-auto=update` with `validate` | ✅ Done |
| 1.10 | **User entity + repository** — `User` JPA entity, `UserRepository` | ✅ Done |
| 1.11 | **JWT auth backend** — `java-jwt` dependency, `JwtService`, `JwtAuthenticationFilter`, `SecurityConfig` | ✅ Done |
| 1.12 | **Auth endpoints** — `POST /auth/register`, `/auth/login`, `/auth/refresh`. BCrypt password hashing, HttpOnly refresh cookie | ✅ Done |
| 1.13 | **User profile endpoint** — `GET/PUT /api/v1/users/me` | ✅ Done |
| 1.14 | **CORS config** — Allow frontend origin in `SecurityConfig` | ✅ Done |
| 1.15 | **Frontend auth context** — `AuthContext` + `AuthProvider`, `fetch` wrapper for JWT + refresh  | ✅ Done |
| 1.16 | **Protected routes** — Redirect unauthenticated users to `/login`, post-login redirect to `/portfolio` | ✅ Done |
| 1.17 | **Connect login UI to backend** — Wire `AuthForm` callbacks to real API calls using `fetch` | ✅ Done |

### Phase 2 — Budgeting Core

**Goal:** Monthly budget-vs-actual model — category CRUD, budget entries, actuals, copy-forward.

> **Model change (PRD v1.1):** Budgeting moved from per-transaction logging to a **monthly budget overview**. A `budget_entry` = one category's `budgeted_amount` + `actual_amount` for a given month. No transactions, recurring scheduler, or history page. See §4.2 of `Moneta_PRD.md`.

| # | Task | Status |
|---|------|--------|
| 2.1 | **Category model + migration** — `V4__create_categories_table.sql`, `Category` entity, `CategoryType` enum, `CategoryRepository` | ✅ Done |
| 2.2 | **Seed preset categories** — Preset income/expense categories seeded via `INSERT` in the V4 migration | ✅ Done |
| 2.3 | **Category CRUD API** — `GET/POST /api/v1/categories`, `DELETE /api/v1/categories/{id}` | ✅ Done |
| 2.4 | **Budget entry model + migration** — `V5__create_budget_entries_table.sql`, `BudgetEntry` entity (category, month, budgeted_amount, actual_amount, currency, notes), `CategoryType` reused, `BudgetEntryRepository`. Old transaction files removed; `CategorySummary` kept | ✅ Done |
| 2.5 | **Budget CRUD API** — `GET/POST /api/v1/budget`, `PUT/DELETE /api/v1/budget/{id}`. One row per category per month; unique `(user, category, month)`; category resolved via `findVisibleById` (presets + own) | ✅ Done |
| 2.6 | **Copy-forward** — `POST /api/v1/budget/copy-forward?month=...` — copies previous month's plan, resets actuals to 0, idempotent | ✅ Done |
| 2.7 | **Monthly budget setup page (frontend)** — Budgeted amount per category, month selector, `react-hook-form` | ⬜ TODO |
| 2.8 | **Actuals entry form (frontend)** — Single form per month; each row shows category + budgeted, with an actual input | ⬜ TODO |
| 2.9 | **Category management UI** — List + add custom categories with colour picker | ⬜ TODO |

### Phase 3 — Budget Dashboard

**Goal:** Budget-vs-actual summary cards, charts, month selector.

| # | Task | Status |
|---|------|--------|
| 3.1 | **Budget summary API** — `GET /api/v1/budget/summary?month=...` (budgeted vs actual totals per type) | ⬜ TODO |
| 3.2 | **Budget report API** — `GET /api/v1/reports/budget` (budget-vs-actual per category + 12-month savings trend) | ⬜ TODO |
| 3.3 | **Dashboard page** — Income (budgeted vs actual), Expenses (budgeted vs actual), Net savings cards | ⬜ TODO |
| 3.4 | **Budget vs Actual bar chart** — Recharts `BarChart`, colour-coded green under / red over budget | ⬜ TODO |
| 3.5 | **12-month savings trend line chart** — Recharts `LineChart` (net savings per month) | ⬜ TODO |
| 3.6 | **Month selector** — Navigate between months, defaults to current month | ⬜ TODO |

### Phase 4 — Portfolio (Manual Assets)

**Goal:** Asset/liability CRUD, portfolio overview, category cards, sidebar layout.

| # | Task | Status |
|---|------|--------|
| 4.1 | **Asset model + migration** — `V6__create_assets_table.sql`, `Asset` entity, `AssetRepository` | ⬜ TODO |
| 4.2 | **Asset CRUD API** — `GET/POST /assets`, `GET/PUT/DELETE /assets/{id}` | ⬜ TODO |
| 4.3 | **Portfolio summary API** — `GET /portfolio/summary` (totals + allocation) | ⬜ TODO |
| 4.4 | **Sidebar layout** — App shell with sidebar nav (Portfolio, Budget, Stocks) | ⬜ TODO |
| 4.5 | **Portfolio overview page** — Net worth hero, 8 asset category cards | ⬜ TODO |
| 4.6 | **Asset allocation donut chart** — Recharts `PieChart` | ⬜ TODO |
| 4.7 | **Manage Assets / Liabilities UI** — Tabs, add/edit/delete forms | ⬜ TODO |

### Phase 5 — Live Pricing

**Goal:** Live stock + crypto prices, stock portfolio detail page.

| # | Task | Status |
|---|------|--------|
| 5.1 | **Stock price service** — Yahoo Finance / Alpha Vantage, 15-min cache | ⬜ TODO |
| 5.2 | **Crypto price service** — CoinGecko API, 15-min cache | ⬜ TODO |
| 5.3 | **Refresh prices endpoint** — `POST /assets/refresh-prices` | ⬜ TODO |
| 5.4 | **Stock portfolio detail page** — Grouped by exchange, P/L%, position% | ⬜ TODO |
| 5.5 | **Edit stock drawer/modal** — Update quantity, purchase price, delete | ⬜ TODO |
| 5.6 | **Fallback UI** — Last known price with warning when API fails | ⬜ TODO |

### Phase 6 — Net Worth Tracking

**Goal:** Monthly snapshots, net worth chart, FIRE toggle.

| # | Task | Status |
|---|------|--------|
| 6.1 | **Snapshot model + migration** — `V7__create_net_worth_snapshots_table.sql` | ⬜ TODO |
| 6.2 | **Monthly snapshot scheduler** — `@Scheduled` + manual trigger `POST /snapshots` | ⬜ TODO |
| 6.3 | **Net worth history API** — `GET /reports/networth` | ⬜ TODO |
| 6.4 | **Net worth over time line chart** — Recharts `LineChart` | ⬜ TODO |
| 6.5 | **FIRE toggle** — Exclude PPOR switch, saved per user | ⬜ TODO |

### Phase 7 — Multi-Currency

**Goal:** FX conversion across the app.

| # | Task | Status |
|---|------|--------|
| 7.1 | **FX rate service** — ExchangeRate-API, daily cache | ⬜ TODO |
| 7.2 | **Currency conversion in aggregation** — Convert to user's preferred currency in summary APIs | ⬜ TODO |
| 7.3 | **Currency selector in settings** — Dropdown in user profile | ⬜ TODO |

### Phase 8 — Polish & Deploy

**Goal:** Production-ready UX and deployment.

| # | Task | Status |
|---|------|--------|
| 8.1 | **Empty states** — Friendly prompts for all empty lists | ⬜ TODO |
| 8.2 | **Loading skeletons** — Skeleton loaders for charts, tables, refreshes | ⬜ TODO |
| 8.3 | **Error boundaries** — React error boundaries | ⬜ TODO |
| 8.4 | **CSV export** — Export budget data (budget vs actual per month) | ⬜ TODO |
| 8.5 | **Docker production build** — Multi-stage Dockerfiles, `docker-compose.prod.yml` | ⬜ TODO |
| 8.6 | **Hosting setup** — Railway / Render / Fly.io | ⬜ TODO |
| 8.7 | **Basic monitoring** — Health check endpoint, structured logging | ⬜ TODO |

---

**Progress: 23 of 55 tasks complete (Phase 1: 17/17
                                     Phase 2: 6/9
                                     Phase 3: 0/6).
** Next up is task 2.7 (Monthly budget setup page — frontend). Backend for Phase 2 is complete.

---