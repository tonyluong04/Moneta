/** Mirrors AssetCategoryTotal — one asset category card on the portfolio overview. */
export interface AssetCategoryTotal {
  categoryId: number;
  name: string;
  /** The card subtitle, e.g. "Bank accounts, physical cash". Nullable in the schema. */
  description: string | null;
  total: number;
}

/** Mirrors PortfolioSummaryResponse — everything the portfolio overview draws. */
export interface PortfolioSummary {
  /** What every amount below is in. Format with this, not the user's profile. */
  currency: string;
  totalAssets: number;
  /** Positive; the hero renders it negative. */
  totalLiabilities: number;
  /** totalAssets − totalLiabilities, so it can be negative. */
  netWorth: number;
  /** All 8 seeded categories in card order, zero totals included. */
  assetCategories: AssetCategoryTotal[];
}
