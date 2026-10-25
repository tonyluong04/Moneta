import { useMemo } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { formatAxisCurrency, formatCurrency } from '@/lib/format';
import { SERIES_ACTUAL, SERIES_BUDGETED } from '@/lib/chartColors';
import type { ExpenseComparison } from '@/types/report';

/**
 * Budgeted against actual, two bars per expense category, for one month.
 *
 * Horizontal (Recharts calls it layout="vertical") because category names like
 * "Entertainment" and "Subscriptions" cannot fit under vertical columns without
 * rotating the labels. Over-budget needs no colour change — the actual bar is
 * simply longer than the budgeted one, and swapping its colour would make colour
 * mean two things at once and break the legend.
 */
export default function BudgetVsActualChart({ expenses }: { expenses: ExpenseComparison[] }) {
  // A row budgeted at 0 and spent at 0 is two invisible bars and a wasted label.
  // Keep "budgeted 50, spent nothing" — that is real information.
  const rows = useMemo(
    () => expenses.filter((expense) => expense.budgeted > 0 || expense.actual > 0),
    [expenses],
  );

  if (rows.length === 0) {
    return <p className="text-sm text-gray-400 py-8 text-center">Nothing budgeted or spent yet.</p>;
  }

  // two 10px bars plus breathing room per category, so bars never squash
  const height = Math.max(200, rows.length * 44 + 48);

  return (
    <ResponsiveContainer width="100%" height={height}>
      <BarChart
        data={rows}
        layout="vertical"
        barGap={4}
        margin={{ top: 4, right: 24, bottom: 4, left: 4 }}
      >
        {/* only the value-axis gridlines; hairline so the bars stay the loudest thing */}
        <CartesianGrid horizontal={false} stroke="#f1f5f9" />
        <XAxis
          type="number"
          tickFormatter={
            // wrapped, not passed directly: recharts calls tickFormatter(value, index),
            // and that index would land on formatAxisCurrency's currency parameter
            (value: number) => formatAxisCurrency(value)
          }
          tick={{ fill: '#94a3b8', fontSize: 11 }}
          axisLine={false}
          tickLine={false}
        />
        <YAxis
          type="category"
          dataKey="name"
          width={112}
          tick={{ fill: '#475569', fontSize: 12 }}
          axisLine={false}
          tickLine={false}
        />
        <Tooltip
          formatter={
            // params left un-annotated so they infer from recharts' own Formatter type,
            // which widens value to string | number | array | undefined
            (value, name) => [formatCurrency(Number(value ?? 0)), String(name)]
          }
          cursor={{ fill: '#f8fafc' }}
          contentStyle={{
            borderRadius: 12,
            border: '1px solid #e2e8f0',
            fontSize: 12,
            boxShadow: '0 4px 12px rgb(0 0 0 / 0.06)',
          }}
        />
        <Legend wrapperStyle={{ fontSize: 12, paddingTop: 8 }} />
        <Bar
          dataKey="budgeted"
          name="Budgeted"
          fill={SERIES_BUDGETED}
          radius={[0, 4, 4, 0]}
          barSize={10}
        />
        <Bar
          dataKey="actual"
          name="Actual"
          fill={SERIES_ACTUAL}
          radius={[0, 4, 4, 0]}
          barSize={10}
        />
      </BarChart>
    </ResponsiveContainer>
  );
}
