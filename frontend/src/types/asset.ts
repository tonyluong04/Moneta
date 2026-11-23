/** 'STOCK' and 'CRYPTO' mark the live-priced categories (Phase 5); null = valued by hand. */
export type PriceSource = 'STOCK' | 'CRYPTO';

/** Mirrors AssetCategoryResponse — what GET /api/v1/assets/categories returns, in card order. */
export interface AssetCategory {
  id: number;
  name: string;
  description: string | null;
  displayOrder: number;
  priceSource: PriceSource | null;
}

/** Mirrors AssetCategorySummary — the trimmed category nested inside an asset. */
export interface AssetCategorySummary {
  id: number;
  name: string;
}

/** Mirrors AssetResponse. */
export interface Asset {
  id: number;
  name: string;
  category: AssetCategorySummary;
  /** The asset's own native currency, which may differ from the user's current preference. */
  currency: string;
  manualValue: number;
  /** What the asset is worth now: manualValue until live pricing exists. */
  value: number;
  /** `value` converted to the user's preferred currency, rounded to its minor unit. */
  displayValue: number;
  /** The user's preferred currency — what displayValue is in. */
  displayCurrency: string;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

/**
 * Mirrors CreateAssetRequest. No currency: the backend stores the user's
 * preferred currency when none is sent (the 7.4 dropdown will start sending one).
 */
export interface CreateAssetRequest {
  name: string;
  categoryId: number;
  manualValue: number;
}

/** Mirrors UpdateAssetRequest — every field optional; no currency, so the row keeps its own. */
export interface UpdateAssetRequest {
  name?: string;
  categoryId?: number;
  manualValue?: number;
}
