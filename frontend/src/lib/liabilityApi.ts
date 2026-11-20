import { apiFetch } from '@/lib/api';
import type {
  CreateLiabilityRequest,
  Liability,
  LiabilityCategory,
  UpdateLiabilityRequest,
} from '@/types/liability';

/** GET /api/v1/liabilities/categories — the 7 seeded categories, in order. */
export function fetchLiabilityCategories(): Promise<LiabilityCategory[]> {
  return apiFetch<LiabilityCategory[]>('/api/v1/liabilities/categories');
}

/** GET /api/v1/liabilities — everything the user owes, mortgages first. */
export function fetchLiabilities(): Promise<Liability[]> {
  return apiFetch<Liability[]>('/api/v1/liabilities');
}

/** POST /api/v1/liabilities — the backend fills in the user's preferred currency. */
export function createLiability(body: CreateLiabilityRequest): Promise<Liability> {
  return apiFetch<Liability>('/api/v1/liabilities', {
    method: 'POST',
    body: JSON.stringify(body),
  });
}

/** PUT /api/v1/liabilities/{id} — partial update; only the fields sent are applied. */
export function updateLiability(id: number, body: UpdateLiabilityRequest): Promise<Liability> {
  return apiFetch<Liability>(`/api/v1/liabilities/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  });
}

/** DELETE /api/v1/liabilities/{id} — 204, no body. */
export function deleteLiability(id: number): Promise<void> {
  return apiFetch<void>(`/api/v1/liabilities/${id}`, { method: 'DELETE' });
}
