export type CategoryType = 'INCOME' | 'EXPENSE';

/** Mirrors CategoryResponse — what GET /api/v1/categories returns. */
export interface Category {
  id: number;
  name: string;
  type: CategoryType;
  colour: string | null;
  preset: boolean;
}

/** Mirrors CategorySummary — the trimmed category nested inside a budget row. */
export interface CategorySummary {
  id: number;
  name: string;
  colour: string | null;
}

/** Mirrors BudgetEntryResponse. */
export interface BudgetEntry {
  id: number;
  category: CategorySummary;
  month: string;          // LocalDate serialises as "2026-09-01"
  budgetedAmount: number;  // BigDecimal serialises as a JSON number
  actualAmount: number;
  currency: string;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

/** Mirrors CreateBudgetEntryRequest. */
export interface CreateBudgetEntryRequest {
  categoryId: number;
  month: string;
  budgetedAmount: number;
  actualAmount?: number;
  currency?: string;
  notes?: string;
}

/** Mirrors UpdateBudgetEntryRequest — every field optional; only what you send is applied. */
export interface UpdateBudgetEntryRequest {
  budgetedAmount?: number;
  actualAmount?: number;
  currency?: string;
  notes?: string;
}