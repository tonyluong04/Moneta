/**
 * Read-only companion to the budget page, at /budget/dashboard?month=YYYY-MM.
 *
 * Layout follows PRD 4.2.3: a calendar block beside the expense pie, then the
 * budgeted-vs-actual bars, then the all-month net line. The first two rows follow
 * the selected month; the line always shows every month with data.
 *
 * One request feeds all three. Each section renders its data as a table for now —
 * tasks 3.3-3.5 add charts above these rather than replacing them, so the table
 * stays as the accessible fallback.
 */
import { useEffect, useState, type ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import MonthSelector from '@/components/budget/MonthSelector';
import BudgetVsActualChart from '@/components/budget/charts/BudgetVsActualChart';
import NetMoneyChart from '@/components/budget/charts/NetMoneyChart';
import ExpenseBreakdownChart from '@/components/budget/charts/ExpenseBreakdownChart';
import { fetchBudgetReport } from '@/lib/budgetApi';
import { formatMonthLabel, toMonthSlug } from '@/lib/month';
import { useMonthParam } from '@/hooks/useMonthParam';
import { cn } from '@/lib/utils';
import type { BudgetReport } from '@/types/report';

interface SectionProps {
  title: string;
  subtitle?: string;
  className?: string;
  children: ReactNode;
}

function Section({ title, subtitle, className, children }: SectionProps) {
  return (
    <div className={cn('bg-white rounded-xl border border-gray-200 p-6', className)}>
      <h2 className="text-sm font-semibold text-gray-900">{title}</h2>
      {subtitle && <p className="text-xs text-gray-400 mt-0.5">{subtitle}</p>}
      <div className="mt-4">{children}</div>
    </div>
  );
}

/**
 * The month, as a tear-off calendar page. Decoration only — no figures, so the
 * numbers on the dashboard all live in the charts beside and below it.
 */
function MonthCard({ month }: { month: Date }) {
  const name = month.toLocaleDateString('en-AU', { month: 'long' });
  const year = month.getFullYear();

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden flex flex-col">
      {/* the coloured binding strip, with two punch holes */}
      <div className="h-10 bg-teal-600 flex items-center justify-center gap-10 shrink-0">
        <span className="w-3 h-3 rounded-full bg-gray-50 shadow-inner" />
        <span className="w-3 h-3 rounded-full bg-gray-50 shadow-inner" />
      </div>

      <div className="flex-1 flex flex-col items-center justify-center py-10 px-4">
        <p
          className="text-4xl lg:text-5xl font-light text-gray-900 uppercase text-center leading-none"
          style={{ letterSpacing: '-0.02em' }}
        >
          {name}
        </p>
        <p className="mt-3 text-sm tracking-[0.3em] text-gray-400">{year}</p>
      </div>
    </div>
  );
}

function Empty({ children }: { children: ReactNode }) {
  return <p className="text-sm text-gray-400 py-8 text-center">{children}</p>;
}

export default function BudgetDashboardPage() {
  // the shared ?month=YYYY-MM contract, also used by the budget table
  const { month, monthParam, setMonth } = useMonthParam();

  const [report, setReport] = useState<BudgetReport | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      try {
        const loaded = await fetchBudgetReport(monthParam);
        if (!cancelled) setReport(loaded);
      } catch (error) {
        if (!cancelled) {
          toast.error(error instanceof Error ? error.message : 'Could not load the report');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [monthParam]);

  const expenses = report?.expenses ?? [];
  const netMoney = report?.netMoney ?? [];
  const totalActualExpenses = expenses.reduce((sum, expense) => sum + expense.actual, 0);

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-5xl mx-auto">
        <div className="flex flex-wrap items-center justify-between gap-4 mb-2">
          <div>
            <h1 className="text-2xl font-semibold text-gray-900">Budget dashboard</h1>
            <p className="text-sm text-gray-500">
              How the plan compared with what actually happened.
            </p>
          </div>

          {/* bare arrows — the calendar block below is what names the month */}
          <MonthSelector
            month={month}
            onChange={setMonth}
            disabled={loading}
            showLabel={false}
          />
        </div>

        <div className="flex items-center gap-3 mb-6">
          <Button asChild variant="ghost" className="px-0 text-teal-700 hover:bg-transparent">
            {/* carry the month across so the two views stay in step */}
            <Link to={`/budget?month=${toMonthSlug(month)}`}>← Edit this month</Link>
          </Button>
        </div>

        {loading ? (
          <div className="bg-white rounded-xl border border-gray-200 p-12 text-center text-gray-400">
            Loading…
          </div>
        ) : (
          <div className="space-y-6">
            {/* ---- Row 1: calendar | Expense Breakdown (pie lands here in 3.5) ---- */}
            <div className="grid gap-6 md:grid-cols-2">
              <MonthCard month={month} />

              <Section title="Expense Breakdown" subtitle="Actual spending by category">
                {totalActualExpenses === 0 ? (
                  <Empty>No spending recorded for {formatMonthLabel(month)}.</Empty>
                ) : (
                  <ExpenseBreakdownChart expenses={expenses} />
                )}
              </Section>
            </div>

            {/* ---- Row 2: Budgeted vs Actual Expenses ---- */}
            <Section title="Budgeted vs Actual Expenses" subtitle={formatMonthLabel(month)}>
              {expenses.length === 0 ? (
                <Empty>No expenses budgeted for {formatMonthLabel(month)}.</Empty>
              ) : (
                <BudgetVsActualChart expenses={expenses} />
              )}
            </Section>

            {/* ---- Row 3: Monthly Money Saved / Lost ---- */}
            <Section
              title="Monthly Money Saved / Lost"
              subtitle="Actual income − actual expenses, every month with data"
            >
              <NetMoneyChart netMoney={netMoney} selectedMonth={report?.month} />
            </Section>
          </div>
        )}
      </div>
    </div>
  );
}
