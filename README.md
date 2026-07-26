# Moneta — Personal Finance & Wealth Management App

Moneta is a full-stack personal finance web application that gives users a unified view of their financial life — combining lightweight monthly budgeting with long-term wealth tracking. Users plan and track monthly budgets (budgeted vs actual), manage assets and liabilities, track a live stock portfolio, and visualise their financial health over time.

> Personal project, work in progress.

## Features

- **Authentication** — email/password with JWT access tokens + rotating refresh tokens (HttpOnly cookie), BCrypt password hashing
- **Categories** — preset income/expense categories seeded at startup, plus user-defined custom categories
- **Budgeting** — a monthly budget-vs-actual model: set a budget per category per month, fill in actuals later, and copy a plan forward from the previous month
- _Planned:_ budget dashboard & charts, asset/liability portfolio, live stock/crypto pricing, net-worth snapshots, multi-currency

## Tech Stack

**Frontend**
- React 19 (TypeScript) + Vite
- Tailwind CSS v4 + shadcn/ui (Radix primitives)
- react-router v7, react-hook-form, Recharts, sonner

**Backend**
- Java 21 + Spring Boot 4 (REST API)
- Spring Security (JWT), Spring Data JPA
- PostgreSQL 16, Flyway migrations
- Plain Java (no Lombok)

**Infrastructure**
- Docker Compose (PostgreSQL)

## Project Structure

```
.
├── backend/            # Spring Boot API (Maven)
├── frontend/           # React + Vite app
└── docker-compose.yml  # PostgreSQL for local development
```

## Getting Started

### Prerequisites

- Java 21, Node.js 20+, and Docker

### 1. Start the database

```bash
docker compose up -d
```

Runs PostgreSQL 16 on port `5433`. Flyway applies the schema migrations automatically on backend startup.

### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

The API runs at `http://localhost:8080`.

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The app runs at `http://localhost:5173`.

## API Overview

Base path: `/api/v1` (auth endpoints are under `/auth`).

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` `/auth/login` `/auth/refresh` | Authentication |
| GET/PUT | `/api/v1/users/me` | Get / update profile |
| GET/POST | `/api/v1/categories` | List / create category |
| DELETE | `/api/v1/categories/{id}` | Delete custom category |
| GET/POST | `/api/v1/budget` | List (by month) / create budget entry |
| PUT/DELETE | `/api/v1/budget/{id}` | Update / delete budget entry |
| POST | `/api/v1/budget/copy-forward` | Copy previous month's plan forward |
