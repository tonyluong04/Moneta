import { apiFetch } from '@/lib/api';
import type { PortfolioSummary } from '@/types/portfolio';

/** GET /api/v1/portfolio/summary — the hero totals and one total per asset category. */
export function fetchPortfolioSummary(): Promise<PortfolioSummary> {
  return apiFetch<PortfolioSummary>('/api/v1/portfolio/summary');
}
