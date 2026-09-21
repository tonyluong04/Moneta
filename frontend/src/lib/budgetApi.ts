import { apiFetch } from '@/lib/api';
import type {
  BudgetEntry,
  Category,
  CreateBudgetEntryRequest,
  UpdateBudgetEntryRequest,
} from '@/types/budget';

/** GET /api/v1/categories — the full seeded set, ordered by type then display order. */
export function fetchCategories(): Promise<Category[]> {
  return apiFetch<Category[]>('/api/v1/categories');
}

/** GET /api/v1/budget?month=YYYY-MM-01 — every budget row for that month. */
export function fetchBudget(month: string): Promise<BudgetEntry[]> {
  return apiFetch<BudgetEntry[]>(`/api/v1/budget?month=${month}`);
}

/** POST /api/v1/budget — first time a category gets a budget for this month. */
export function createBudgetEntry(body: CreateBudgetEntryRequest): Promise<BudgetEntry> {
  return apiFetch<BudgetEntry>('/api/v1/budget', {
    method: 'POST',
    body: JSON.stringify(body),
  });
}

/** PUT /api/v1/budget/{id} — change an existing row's amounts. */
export function updateBudgetEntry(
  id: number,
  body: UpdateBudgetEntryRequest,
): Promise<BudgetEntry> {
  return apiFetch<BudgetEntry>(`/api/v1/budget/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  });
}

/** DELETE /api/v1/budget/{id} — 204, no body. */
export function deleteBudgetEntry(id: number): Promise<void> {
  return apiFetch<void>(`/api/v1/budget/${id}`, { method: 'DELETE' });
}

/** POST /api/v1/budget/copy-forward?month=... — clones last month's plan, actuals reset to 0. */
export function copyForward(month: string): Promise<BudgetEntry[]> {
  return apiFetch<BudgetEntry[]>(`/api/v1/budget/copy-forward?month=${month}`, {
    method: 'POST',
  });
}