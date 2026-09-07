/** Budget entry form */
import { useEffect, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { formatCurrency } from '@/lib/format';
import type { Category } from '@/types/budget';

/** One editable line: a category, the budget row it maps to (if any), and the typed amount. */
export interface BudgetRow {
  categoryId: number;
  entryId: number | null; // null = no budget row on the server yet
  amount: string;         // kept as a string because inputs are strings
}

export interface BudgetSetupFormValues {
  rows: BudgetRow[];
}

interface BudgetSetupFormProps {
  categories: Category[];
  rows: BudgetRow[];
  saving: boolean;
  onSave: (values: BudgetSetupFormValues) => Promise<void>;
}

/** Mirrors the backend's @DecimalMin(0) + @Digits(fraction = 2) so bad input never leaves the browser. */
function validateAmount(value: string): true | string {
  const trimmed = value.trim();
  if (trimmed === '') return true; // blank is allowed — it means 0
  if (!/^\d+(\.\d{1,2})?$/.test(trimmed)) {
    return 'Use a positive number with at most 2 decimals';
  }
  return true;
}

export default function BudgetSetupForm({
  categories,
  rows,
  saving,
  onSave,
}: BudgetSetupFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors, isDirty },
  } = useForm<BudgetSetupFormValues>({ defaultValues: { rows } });

  // The parent hands us fresh rows whenever the month changes or a save finishes.
  // reset() refills every input and clears the dirty flag.
  useEffect(() => {
    reset({ rows });
  }, [rows, reset]);

  const categoryById = useMemo(
    () => new Map(categories.map((category) => [category.id, category])),
    [categories],
  );

  // Split row positions into the two sections. We render by index because
  // react-hook-form addresses fields by path: rows.0.amount, rows.1.amount, ...
  const { incomeIndexes, expenseIndexes } = useMemo(() => {
    const income: number[] = [];
    const expense: number[] = [];
    rows.forEach((row, index) => {
      if (categoryById.get(row.categoryId)?.type === 'INCOME') {
        income.push(index);
      } else {
        expense.push(index);
      }
    });
    return { incomeIndexes: income, expenseIndexes: expense };
  }, [rows, categoryById]);

  // watch() re-renders on every keystroke so the totals below stay live.
  const watchedRows = watch('rows');

  const totalOf = (indexes: number[]) =>
    indexes.reduce((sum, index) => {
      const amount = Number(watchedRows?.[index]?.amount);
      return sum + (Number.isFinite(amount) ? amount : 0);
    }, 0);

  const totalIncome = totalOf(incomeIndexes);
  const totalExpenses = totalOf(expenseIndexes);
  const plannedSavings = totalIncome - totalExpenses;

  const renderSection = (title: string, indexes: number[]) => (
    <div className="bg-white rounded-xl border border-gray-200 p-6">
      <h3 className="text-sm font-semibold text-gray-900 mb-4">{title}</h3>
      <div className="space-y-3">
        {indexes.map((index) => {
          const row = rows[index];
          const category = categoryById.get(row.categoryId);
          const fieldError = errors.rows?.[index]?.amount;

          return (
            <div key={row.categoryId} className="flex items-center gap-3">
              <span
                className="w-2.5 h-2.5 rounded-full shrink-0"
                style={{ backgroundColor: category?.colour ?? '#94a3b8' }}
              />
              <Label
                htmlFor={`amount-${row.categoryId}`}
                className="flex-1 text-sm text-gray-700 font-normal"
              >
                {category?.name}
              </Label>
              <div className="w-44">
                <Input
                  id={`amount-${row.categoryId}`}
                  inputMode="decimal"
                  placeholder="0.00"
                  className="rounded-xl text-right border-gray-200 bg-white text-sm"
                  {...register(`rows.${index}.amount` as const, { validate: validateAmount })}
                />
                {fieldError && (
                  <p className="text-xs text-red-600 mt-1 text-right">{fieldError.message}</p>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );

  return (
    <form onSubmit={handleSubmit(onSave)} className="space-y-6">
      {renderSection('Expected income', incomeIndexes)}
      {renderSection('Spending budget', expenseIndexes)}

      <div className="bg-white rounded-xl border border-gray-200 p-6 flex flex-wrap items-center gap-8">
        <div>
          <p className="text-xs text-gray-500">Planned income</p>
          <p className="text-lg font-semibold text-teal-700">{formatCurrency(totalIncome)}</p>
        </div>
        <div>
          <p className="text-xs text-gray-500">Planned expenses</p>
          <p className="text-lg font-semibold text-gray-900">{formatCurrency(totalExpenses)}</p>
        </div>
        <div>
          <p className="text-xs text-gray-500">Planned savings</p>
          <p
            className={
              plannedSavings >= 0
                ? 'text-lg font-semibold text-teal-700'
                : 'text-lg font-semibold text-red-600'
            }
          >
            {formatCurrency(plannedSavings)}
          </p>
        </div>

        <Button
          type="submit"
          disabled={saving || !isDirty}
          className="ml-auto bg-teal-600 text-white rounded-xl hover:bg-teal-700 font-semibold px-6"
        >
          {saving ? 'Saving…' : 'Save budget'}
        </Button>
      </div>
    </form>
  );
}

