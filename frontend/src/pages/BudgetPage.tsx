/**
 * The budget page: one table per month holding both the plan and the actuals.
 * The selected month lives in the URL (/budget?month=2026-03) so the view is
 * linkable and survives a refresh.
 */
import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import MonthSelector from '@/components/budget/MonthSelector';
import BudgetTable, {
  type BudgetRow,
  type BudgetTableValues,
} from '@/components/budget/BudgetTable';
import {
  copyForward,
  createBudgetEntry,
  fetchBudget,
  fetchCategories,
  updateBudgetEntry,
} from '@/lib/budgetApi';
import { parseAmount } from '@/lib/format';
import {
  addMonths,
  currentMonth,
  formatMonthLabel,
  isCurrentMonth,
  monthElapsed,
  parseMonthSlug,
  toMonthParam,
  toMonthSlug,
} from '@/lib/month';
import type { BudgetEntry, Category } from '@/types/budget';

export default function BudgetPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  // ?month=2026-03 is the source of truth; anything missing or malformed
  // falls back to the current month rather than rendering an Invalid Date.
  const month = parseMonthSlug(searchParams.get('month')) ?? currentMonth();
  const monthParam = toMonthParam(month); // the API wants 2026-03-01

  const [categories, setCategories] = useState<Category[]>([]);
  const [entries, setEntries] = useState<BudgetEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const changeMonth = (next: Date) => setSearchParams({ month: toMonthSlug(next) });

  const load = useCallback(async () => {
    const [loadedCategories, loadedEntries] = await Promise.all([
      fetchCategories(),
      fetchBudget(monthParam),
    ]);
    setCategories(loadedCategories);
    setEntries(loadedEntries);
  }, [monthParam]);

  // Reload whenever the month in the URL changes.
  useEffect(() => {
    let cancelled = false;

    async function loadForMonth() {
      setLoading(true);
      try {
        const [loadedCategories, loadedEntries] = await Promise.all([
          fetchCategories(),
          fetchBudget(monthParam),
        ]);
        if (!cancelled) {
          setCategories(loadedCategories);
          setEntries(loadedEntries);
        }
      } catch (error) {
        if (!cancelled) {
          toast.error(error instanceof Error ? error.message : 'Could not load the budget');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    loadForMonth();
    return () => {
      cancelled = true;
    };
  }, [monthParam]);

  /**
   * One row per category, in the order the API sent them (type, then display_order).
   * Categories with a stored budget row carry its id so we PUT; the rest carry null
   * and get POSTed on first save.
   */
  const rows = useMemo<BudgetRow[]>(() => {
    const entryByCategoryId = new Map(entries.map((entry) => [entry.category.id, entry]));

    return categories.map((category) => {
      const entry = entryByCategoryId.get(category.id);
      const budgeted = entry ? String(entry.budgetedAmount) : '';
      let actual = entry ? String(entry.actualAmount) : '';

      // A fixed category costs the same every month, so seed its actual from the
      // plan. Only when nothing real is stored yet, so a saved override survives.
      if (category.fixed && parseAmount(actual) === 0) {
        actual = budgeted;
      }

      return {
        categoryId: category.id,
        entryId: entry ? entry.id : null,
        budgeted,
        actual,
      };
    });
  }, [categories, entries]);

  const handleSave = async (values: BudgetTableValues) => {
    setSaving(true);
    try {
      const entryByCategoryId = new Map(entries.map((entry) => [entry.category.id, entry]));
      const requests: Promise<unknown>[] = [];

      // Diff against what the server holds, not against the form defaults — that
      // way an auto-filled fixed actual still counts as a change worth saving.
      rows.forEach((row, index) => {
        const typedBudgeted = parseAmount(values.rows[index]?.budgeted ?? '');
        const typedActual = parseAmount(values.rows[index]?.actual ?? '');

        const entry = entryByCategoryId.get(row.categoryId);
        const savedBudgeted = entry ? Number(entry.budgetedAmount) : 0;
        const savedActual = entry ? Number(entry.actualAmount) : 0;

        if (typedBudgeted === savedBudgeted && typedActual === savedActual) return;

        if (!entry) {
          if (typedBudgeted === 0 && typedActual === 0) return; // nothing worth creating
          requests.push(
            createBudgetEntry({
              categoryId: row.categoryId,
              month: monthParam,
              budgetedAmount: typedBudgeted,
              actualAmount: typedActual,
            }),
          );
        } else {
          requests.push(
            updateBudgetEntry(entry.id, {
              budgetedAmount: typedBudgeted,
              actualAmount: typedActual,
            }),
          );
        }
      });

      if (requests.length === 0) {
        toast.info('No changes to save');
        return;
      }

      await Promise.all(requests);
      toast.success(`Saved ${formatMonthLabel(month)}`);
      await load(); // refetch so new rows pick up their server ids
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Could not save the budget');
    } finally {
      setSaving(false);
    }
  };

  const handleCopyForward = async () => {
    setSaving(true);
    try {
      const created = await copyForward(monthParam);
      if (created.length === 0) {
        toast.info(
          `Nothing to copy — ${formatMonthLabel(addMonths(month, -1))} has no budget, ` +
            'or this month already has one.',
        );
      } else {
        toast.success(
          `Copied ${created.length} categories from ${formatMonthLabel(addMonths(month, -1))}`,
        );
        await load();
      }
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Could not copy last month');
    } finally {
      setSaving(false);
    }
  };

  const elapsed = monthElapsed(month);

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-5xl mx-auto">
        <div className="flex flex-wrap items-center justify-between gap-4 mb-2">
          <div>
            <h1 className="text-2xl font-semibold text-gray-900">Budget</h1>
            <p className="text-sm text-gray-500">
              Plan what you expect to earn and spend, then fill in the actuals as the month goes.
            </p>
          </div>
          <MonthSelector month={month} onChange={changeMonth} disabled={loading || saving} />
        </div>

        <div className="flex items-center gap-3 mb-6">
          <Button asChild variant="ghost" className="px-0 text-teal-700 hover:bg-transparent">
            <Link to="/portfolio">← Back to portfolio</Link>
          </Button>

          {/* Pace indicator — only meaningful for the month we are living in. */}
          {isCurrentMonth(month) && (
            <span className="text-xs text-gray-500">
              Day {elapsed.day} of {elapsed.daysInMonth} — {elapsed.percent}% elapsed
            </span>
          )}

          <Button
            type="button"
            variant="outline"
            disabled={loading || saving}
            onClick={handleCopyForward}
            className="ml-auto rounded-xl border-teal-200 text-teal-700 hover:bg-teal-50"
          >
            Copy {formatMonthLabel(addMonths(month, -1))}
          </Button>
        </div>

        {loading ? (
          <div className="bg-white rounded-xl border border-gray-200 p-12 text-center text-gray-400">
            Loading…
          </div>
        ) : (
          <>
            {entries.length === 0 && (
              <div className="bg-teal-50 border border-teal-200 text-teal-800 rounded-xl px-4 py-3 text-sm mb-6">
                No budget set for {formatMonthLabel(month)} yet — copy last month, or fill in the
                amounts below and save.
              </div>
            )}
            <BudgetTable
              categories={categories}
              rows={rows}
              saving={saving}
              onSave={handleSave}
            />
          </>
        )}
      </div>
    </div>
  );
}
