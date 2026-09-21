import { useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Pencil } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import ProgressBar from '@/components/budget/ProgressBar';
import { formatCurrency, parseAmount } from '@/lib/format';
import { cn } from '@/lib/utils';
import type { Category, CategoryType } from '@/types/budget';

/** One editable line: a category, the budget row it maps to (if any), and both typed amounts. */
export interface BudgetRow {
  categoryId: number;
  entryId: number | null; // null = no budget row on the server yet
  budgeted: string;       // kept as strings because inputs are strings
  actual: string;
}

export interface BudgetTableValues {
  rows: BudgetRow[];
}

interface BudgetTableProps {
  categories: Category[];
  rows: BudgetRow[];
  saving: boolean;
  onSave: (values: BudgetTableValues) => Promise<void>;
}

/** Mirrors the backend's @DecimalMin(0) + @Digits(fraction = 2). */
function validateAmount(value: string): true | string {
  const trimmed = value.trim();
  if (trimmed === '') return true; // blank means 0
  if (!/^\d+(\.\d{1,2})?$/.test(trimmed)) return 'Use a positive number, max 2 decimals';
  return true;
}

const GRID = 'grid grid-cols-[minmax(0,1fr)_8rem_11rem_11rem] items-center gap-4';

export default function BudgetTable({ categories, rows, saving, onSave }: BudgetTableProps) {
  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<BudgetTableValues>({ defaultValues: { rows } });

  // Fixed rows render read-only; clicking the pencil unlocks one for this session.
  const [overridden, setOverridden] = useState<Set<number>>(new Set());

  // The parent hands us fresh rows on month change or after a save.
  useEffect(() => {
    reset({ rows });
    setOverridden(new Set());
  }, [rows, reset]);

  const categoryById = useMemo(
    () => new Map(categories.map((category) => [category.id, category])),
    [categories],
  );

  // Row positions grouped by type, and within a type by fixed vs variable.
  // Order inside each bucket is the server's display_order, untouched.
  const sections = useMemo(() => {
    const grouped: Record<CategoryType, { fixed: number[]; variable: number[] }> = {
      INCOME: { fixed: [], variable: [] },
      EXPENSE: { fixed: [], variable: [] },
    };

    rows.forEach((row, index) => {
      const category = categoryById.get(row.categoryId);
      if (!category) return;
      const bucket = grouped[category.type];
      if (category.fixed) bucket.fixed.push(index);
      else bucket.variable.push(index);
    });

    return grouped;
  }, [rows, categoryById]);

  // watch() re-renders per keystroke, which drives the progress bars and totals.
  const watchedRows = watch('rows');

  const amountAt = (index: number, field: 'budgeted' | 'actual') =>
    parseAmount(watchedRows?.[index]?.[field] ?? '');

  const totalOf = (indexes: number[], field: 'budgeted' | 'actual') =>
    indexes.reduce((sum, index) => sum + amountAt(index, field), 0);

  const allOf = (type: CategoryType) => [...sections[type].fixed, ...sections[type].variable];

  const incomeBudgeted = totalOf(allOf('INCOME'), 'budgeted');
  const incomeActual = totalOf(allOf('INCOME'), 'actual');
  const expenseBudgeted = totalOf(allOf('EXPENSE'), 'budgeted');
  const expenseActual = totalOf(allOf('EXPENSE'), 'actual');

  const unlock = (categoryId: number) =>
    setOverridden((previous) => new Set(previous).add(categoryId));

  const renderRow = (index: number) => {
    const row = rows[index];
    const category = categoryById.get(row.categoryId);
    if (!category) return null;

    const isLocked = category.fixed && !overridden.has(row.categoryId);
    const budgetedError = errors.rows?.[index]?.budgeted;
    const actualError = errors.rows?.[index]?.actual;

    return (
      <div key={row.categoryId} className={cn(GRID, 'py-2')}>
        {/* CATEGORY */}
        <div className="flex items-center gap-2 min-w-0">
          <span
            className="w-2.5 h-2.5 rounded-full shrink-0"
            style={{ backgroundColor: category.colour ?? '#94a3b8' }}
          />
          <span className="text-sm text-gray-800 truncate">{category.name}</span>
        </div>

        {/* BUDGETED - always editable, styled quietly since it is set once a month */}
        <div>
          <div className="flex items-center gap-1">
            <span className="text-xs text-gray-400">A$</span>
            <Input
              aria-label={`${category.name} budgeted`}
              inputMode="decimal"
              placeholder="0.00"
              className="h-8 rounded-lg text-right text-sm border-gray-200 bg-white"
              {...register(`rows.${index}.budgeted` as const, { validate: validateAmount })}
            />
          </div>
          {budgetedError && (
            <p className="text-[11px] text-red-600 mt-1 text-right">{budgetedError.message}</p>
          )}
        </div>

        {/* ACTUAL - a plain input when variable; pre-filled and locked when fixed */}
        <div>
          <div className="flex items-center gap-1">
            <span className={cn('text-xs', isLocked ? 'text-gray-300' : 'text-gray-400')}>A$</span>
            <Input
              aria-label={`${category.name} actual`}
              inputMode="decimal"
              placeholder="0.00"
              readOnly={isLocked}
              tabIndex={isLocked ? -1 : undefined}
              className={cn(
                'h-8 rounded-lg text-right text-sm',
                isLocked
                  ? 'border-transparent bg-transparent shadow-none text-gray-500 cursor-default focus-visible:ring-0'
                  : 'border-gray-300 bg-white',
              )}
              {...register(`rows.${index}.actual` as const, { validate: validateAmount })}
            />
            {category.fixed && (
              <>
                <span className="text-[10px] uppercase tracking-wide text-gray-400 shrink-0">
                  fixed
                </span>
                {isLocked && (
                  <button
                    type="button"
                    onClick={() => unlock(row.categoryId)}
                    aria-label={`Override ${category.name} actual`}
                    className="text-gray-400 hover:text-teal-600 cursor-pointer shrink-0"
                  >
                    <Pencil className="w-3.5 h-3.5" />
                  </button>
                )}
              </>
            )}
          </div>
          {actualError && (
            <p className="text-[11px] text-red-600 mt-1 text-right">{actualError.message}</p>
          )}
        </div>

        {/* PROGRESS */}
        <ProgressBar actual={amountAt(index, 'actual')} budgeted={amountAt(index, 'budgeted')} />
      </div>
    );
  };

  const renderSection = (title: string, type: CategoryType) => {
    const { fixed, variable } = sections[type];
    if (fixed.length === 0 && variable.length === 0) return null;

    return (
      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <div className="px-6 pt-5 pb-2">
          <h2 className="text-sm font-semibold text-gray-900">{title}</h2>
        </div>

        <div className={cn(GRID, 'px-6 pb-2 border-b border-gray-100')}>
          <span className="text-[11px] uppercase tracking-wide text-gray-400">Category</span>
          <span className="text-[11px] uppercase tracking-wide text-gray-400 text-right">
            Budgeted
          </span>
          <span className="text-[11px] uppercase tracking-wide text-gray-400 text-right">
            Actual
          </span>
          <span className="text-[11px] uppercase tracking-wide text-gray-400">Progress</span>
        </div>

        <div className="px-6 py-2">{fixed.map(renderRow)}</div>

        {/* the divider from the layout: fixed commitments above, variable spending below */}
        {fixed.length > 0 && variable.length > 0 && <div className="border-t border-gray-200" />}

        <div className="px-6 py-2">{variable.map(renderRow)}</div>
      </div>
    );
  };

  const summary = (label: string, budgeted: number, actual: number) => (
    <div>
      <p className="text-xs text-gray-500">{label}</p>
      <p className="text-lg font-semibold text-gray-900 tabular-nums">{formatCurrency(actual)}</p>
      <p className="text-xs text-gray-400 tabular-nums">of {formatCurrency(budgeted)} budgeted</p>
    </div>
  );

  const netActual = incomeActual - expenseActual;

  return (
    <form onSubmit={handleSubmit(onSave)} className="space-y-6">
      {renderSection('Income', 'INCOME')}
      {renderSection('Expenses', 'EXPENSE')}

      <div className="bg-white rounded-xl border border-gray-200 p-6 flex flex-wrap items-end gap-10">
        {summary('Income', incomeBudgeted, incomeActual)}
        {summary('Expenses', expenseBudgeted, expenseActual)}
        <div>
          <p className="text-xs text-gray-500">Net savings</p>
          <p
            className={cn(
              'text-lg font-semibold tabular-nums',
              netActual >= 0 ? 'text-teal-700' : 'text-red-600',
            )}
          >
            {formatCurrency(netActual)}
          </p>
          <p className="text-xs text-gray-400 tabular-nums">
            of {formatCurrency(incomeBudgeted - expenseBudgeted)} planned
          </p>
        </div>

        <Button
          type="submit"
          disabled={saving}
          className="ml-auto bg-teal-600 text-white rounded-xl hover:bg-teal-700 font-semibold px-6"
        >
          {saving ? 'Saving…' : 'Save'}
        </Button>
      </div>
    </form>
  );
}
