/**
 * The signed-in home page, at /portfolio (PRD §4.3.1): the net worth hero, then
 * one card per asset category with its share of total assets as a ring, then the
 * panel that adds, edits and deletes assets and liabilities.
 *
 * One request feeds both. Every amount is formatted with the currency the
 * response carries, not the profile's, so the label always matches the numbers.
 * The net-worth-over-time line arrives with snapshots in Phase 6.
 *
 * Every change in the manage panel bumps `reloadKey`, which refetches the summary
 * so the hero and rings follow along. Only the first load shows "Loading…".
 */
import { useCallback, useEffect, useState } from 'react';
import { toast } from 'sonner';
import NetWorthHero from '@/components/portfolio/NetWorthHero';
import CategoryCard from '@/components/portfolio/CategoryCard';
import ManagePanel from '@/components/portfolio/manage/ManagePanel';
import { fetchPortfolioSummary } from '@/lib/portfolioApi';
import type { PortfolioSummary } from '@/types/portfolio';

export default function PortfolioPage() {
  const [summary, setSummary] = useState<PortfolioSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [reloadKey, setReloadKey] = useState(0);
  const reload = useCallback(() => setReloadKey((key) => key + 1), []);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        const loaded = await fetchPortfolioSummary();
        if (!cancelled) setSummary(loaded);
      } catch (error) {
        if (!cancelled) {
          toast.error(error instanceof Error ? error.message : 'Could not load the portfolio');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [reloadKey]);

  if (loading) {
    return (
      <div className="bg-white rounded-xl border border-gray-200 p-12 text-center text-gray-400">
        Loading…
      </div>
    );
  }

  if (!summary) {
    // the toast already said what went wrong
    return (
      <div className="bg-white rounded-xl border border-gray-200 p-12 text-center text-gray-400">
        The portfolio could not be loaded.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <NetWorthHero summary={summary} />

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {summary.assetCategories.map((category) => (
          <CategoryCard
            key={category.categoryId}
            category={category}
            totalAssets={summary.totalAssets}
            currency={summary.currency}
          />
        ))}
      </div>

      <ManagePanel currency={summary.currency} onChanged={reload} />
    </div>
  );
}
