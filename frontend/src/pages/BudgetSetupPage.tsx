/** Budget page */
import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import MonthSelector from '@/components/budget/MonthSelector';
import BudgetSetupForm, {
  type BudgetRow,
  type BudgetSetupFormValues,
} from '@/components/budget/BudgetSetupForm';
import {
  copyForward,
  createBudgetEntry,
  fetchBudget,
  fetchCategories,
  updateBudgetEntry,
} from '@/lib/budgetApi';
import { parseAmount } from '@/lib/format';
import { addMonths, currentMonth, formatMonthLabel, toMonthParam } from '@/lib/month';
import type { BudgetEntry, Category } from '@/types/budget';

export default function BudgetSetupPage() {
  const [month, setMonth] = useState<Date>(currentMonth);
  const [categories, setCategories] = useState<Category[]>([]);
  const [entries, setEntries] = useState<BudgetEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const monthParam = toMonthParam(month);

  const load = useCallback(async () => {
    const [loadedCategories, loadedEntries] = await Promise.all([
      fetchCategories(),
      fetchBudget(monthParam),
    ]);
    setCategories(loadedCategories);
    setEntries(loadedEntries);
  }, [monthParam]);

  // Reload whenever the selected month changes.
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
   * One row per visible category. Categories with an existing budget row carry its
   * id (so we PUT); the rest carry null (so we POST) and start blank.
   */
  const rows = useMemo<BudgetRow[]>(() => {
    const entryByCategoryId = new Map(entries.map((entry) => [entry.category.id, entry]));

    return categories.map((category) => {
      const entry = entryByCategoryId.get(category.id);
      return {
        categoryId: category.id,
        entryId: entry ? entry.id : null,
        amount: entry ? String(entry.budgetedAmount) : '',
      };
    });
  }, [categories, entries]);

  const handleSave = async (values: BudgetSetupFormValues) => {
    setSaving(true);
    try {
      const requests: Promise<unknown>[] = [];

      // Pair each typed value with its identity by index, and only send what changed.
      rows.forEach((row, index) => {
        const typedAmount = parseAmount(values.rows[index]?.amount ?? '');
        const savedAmount = parseAmount(row.amount);

        if (typedAmount === savedAmount) return; // untouched

        if (row.entryId === null) {
          if (typedAmount === 0) return; // nothing worth creating
          requests.push(
            createBudgetEntry({
              categoryId: row.categoryId,
              month: monthParam,
              budgetedAmount: typedAmount,
            }),
          );
        } else {
          requests.push(updateBudgetEntry(row.entryId, { budgetedAmount: typedAmount }));
        }
      });

      if (requests.length === 0) {
        toast.info('No changes to save');
        return;
      }

      await Promise.all(requests);
      toast.success(`Budget saved for ${formatMonthLabel(month)}`);
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

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-3xl mx-auto">
        <div className="flex flex-wrap items-center justify-between gap-4 mb-2">
          <div>
            <h1 className="text-2xl font-semibold text-gray-900">Monthly budget</h1>
            <p className="text-sm text-gray-500">
              Set what you expect to earn and spend this month.
            </p>
          </div>
          <MonthSelector month={month} onChange={setMonth} disabled={loading || saving} />
        </div>

        <div className="flex items-center gap-3 mb-6">
          <Button asChild variant="ghost" className="px-0 text-teal-700 hover:bg-transparent">
            <Link to="/portfolio">← Back to portfolio</Link>
          </Button>
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
            <BudgetSetupForm
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