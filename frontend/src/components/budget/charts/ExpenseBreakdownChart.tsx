import { useMemo } from 'react';
import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from 'recharts';
import { formatCurrency } from '@/lib/format';
import { PIE_OTHER, PIE_SLICES } from '@/lib/chartColors';
import type { ExpenseComparison } from '@/types/report';

interface Slice {
  name: string;
  value: number;
  colour: string;
}

interface SliceTooltipProps {
  active?: boolean;
  payload?: Array<{ payload: Slice }>;
  total: number;
}

function SliceTooltip({ active, payload, total }: SliceTooltipProps) {
  const slice = payload?.[0]?.payload;
  if (!active || !slice) return null;

  return (
    <div className="rounded-xl border border-gray-200 bg-white px-3 py-2 text-xs shadow-md">
      <p className="font-semibold text-gray-900">{slice.name}</p>
      <p className="mt-0.5 text-gray-600 tabular-nums">
        {formatCurrency(slice.value)} · {Math.round((slice.value / total) * 100)}%
      </p>
    </div>
  );
}

/**
 * Actual spending by category for one month, as a donut.
 *
 * Capped at the top four categories plus an "Other" bucket. Two reasons: a pie only
 * reads as part-to-whole at roughly six slices, and no five-hue palette passed the
 * all-pairs colourblind separation check — which is the right test here, because a
 * reader compares every slice against every other, not just its neighbours.
 *
 * Donut rather than solid pie so the total has somewhere to live; the hole also
 * makes the angles easier to judge.
 */
export default function ExpenseBreakdownChart({ expenses }: { expenses: ExpenseComparison[] }) {
  const slices = useMemo<Slice[]>(() => {
    const ranked = expenses
      .filter((expense) => expense.actual > 0)
      .sort((a, b) => b.actual - a.actual);

    // hues are assigned in fixed order by rank position, never cycled
    const top: Slice[] = ranked.slice(0, PIE_SLICES.length).map((expense, index) => ({
      name: expense.name,
      value: expense.actual,
      colour: PIE_SLICES[index],
    }));

    const rest = ranked.slice(PIE_SLICES.length);
    if (rest.length > 0) {
      top.push({
        // "Remaining", not "Other" — there is a seeded category literally called
        // "Other Expense", and two things named Other in one legend is unreadable
        name: `Remaining (${rest.length})`,
        value: rest.reduce((sum, expense) => sum + expense.actual, 0),
        colour: PIE_OTHER,
      });
    }
    return top;
  }, [expenses]);

  const total = slices.reduce((sum, slice) => sum + slice.value, 0);

  if (slices.length === 0) {
    return <p className="text-sm text-gray-400 py-8 text-center">No spending recorded yet.</p>;
  }

  return (
    <div>
      <div className="relative">
        <ResponsiveContainer width="100%" height={200}>
          <PieChart>
            <Pie
              data={slices}
              dataKey="value"
              nameKey="name"
              innerRadius={56}
              outerRadius={86}
              /* the gap plus the white ring keep adjacent fills separable */
              paddingAngle={2}
              stroke="#ffffff"
              strokeWidth={2}
            >
              {slices.map((slice) => (
                <Cell key={slice.name} fill={slice.colour} />
              ))}
            </Pie>
            <Tooltip content={<SliceTooltip total={total} />} />
          </PieChart>
        </ResponsiveContainer>

        {/* total in the hole — answers "68% of what?" without a separate panel.
            pointer-events-none so it never swallows a slice hover. */}
        <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
          <p className="text-lg font-semibold text-gray-900 tabular-nums">
            {formatCurrency(total)}
          </p>
          <p className="text-[11px] uppercase tracking-wide text-gray-400">spent</p>
        </div>
      </div>

      {/* our own legend rather than recharts': identity is never colour-alone, and
          this way the amounts use the same currency formatting as everywhere else */}
      <ul className="mt-4 space-y-1.5">
        {slices.map((slice) => (
          <li key={slice.name} className="flex items-center gap-2 text-sm">
            <span
              className="w-2.5 h-2.5 rounded-full shrink-0"
              style={{ backgroundColor: slice.colour }}
            />
            <span className="flex-1 text-gray-700 truncate">{slice.name}</span>
            <span className="text-gray-800 tabular-nums">{formatCurrency(slice.value)}</span>
            <span className="w-10 text-right text-gray-400 tabular-nums">
              {Math.round((slice.value / total) * 100)}%
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
