/** Mirrors LiabilityCategoryResponse — what GET /api/v1/liabilities/categories returns, in order. */
export interface LiabilityCategory {
  id: number;
  name: string;
  displayOrder: number;
}

/** Mirrors LiabilityCategorySummary — the trimmed category nested inside a liability. */
export interface LiabilityCategorySummary {
  id: number;
  name: string;
}

/** Mirrors LiabilityResponse. */
export interface Liability {
  id: number;
  name: string;
  category: LiabilityCategorySummary;
  /** The liability's own native currency. */
  currency: string;
  /** Positive amount owing; the UI renders it negative. */
  balance: number;
  /** Annual percentage, e.g. 6.24. Null when not given. */
  interestRate: number | null;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

/** Mirrors CreateLiabilityRequest. No currency: the backend uses the user's preferred one. */
export interface CreateLiabilityRequest {
  name: string;
  categoryId: number;
  balance: number;
  interestRate?: number | null;
}

/** Mirrors UpdateLiabilityRequest — every field optional; no currency, so the row keeps its own. */
export interface UpdateLiabilityRequest {
  name?: string;
  categoryId?: number;
  balance?: number;
  interestRate?: number | null;
}
