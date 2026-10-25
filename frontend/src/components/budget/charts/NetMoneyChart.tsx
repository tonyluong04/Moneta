import { useMemo } from 'react';
import {
  CartesianGrid,
  Line,
  LineChart,
  ReferenceDot,
  ReferenceLine,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { formatAxisCurrency, formatCurrency } from '@/lib/format';
import { LINE_NET } from '@/lib/chartColors';
import { formatMonthShort } from '@/lib/month';
import type { MonthlyNet } from '@/types/report';

interface NetMoneyChartProps {
  netMoney: MonthlyNet[];
  /** The dashboard's selected month ("2026-09-01"), marked on the line as a "you are here". */
  selectedMonth?: string;
}

interface NetTooltipProps {
  active?: boolean;
  payload?: Array<{ payload: MonthlyNet }>;
}

/**
 * All three numbers, not just the net. A net of A$1,560 is far more useful once
 * you can see it came from A$2,000 in and A$440 out.
 */
function NetTooltip({ active, payload }: NetTooltipProps) {
  const point = payload?.[0]?.payload;
  if (!active || !point) return null;

  const saved = point.net >= 0;

  return (
    <div className="rounded-xl border border-gray-200 bg-white px-3 py-2 text-xs shadow-md">
      <p className="font-semibold text-gray-900">{formatMonthShort(point.month)}</p>
      <dl className="mt-1.5 space-y-0.5 tabular-nums">
        <div className="flex justify-between gap-6">
          <dt className="text-gray-500">Income</dt>
          <dd className="text-gray-800">{formatCurrency(point.income)}</dd>
        </div>
        <div className="flex justify-between gap-6">
          <dt className="text-gray-500">Expenses</dt>
          <dd className="text-gray-800">{formatCurrency(point.expenses)}</dd>
        </div>
        <div className="flex justify-between gap-6 border-t border-gray-100 pt-1 mt-1">
          <dt className="text-gray-500">{saved ? 'Saved' : 'Lost'}</dt>
          <dd className={saved ? 'font-medium text-teal-700' : 'font-medium text-red-600'}>
            {formatCurrency(Math.abs(point.net))}
          </dd>
        </div>
      </dl>
    </div>
  );
}

/**
 * Net money (actual income − actual expenses) for every month with data.
 *
 * Deliberately ignores the month selector for its data — the point of this chart
 * is the whole trend. The selected month is only marked, not filtered to.
 *
 * Sign is encoded by position against a dashed zero baseline rather than by a
 * green/red line: those are reserved status colours, and "below the line" is
 * already unmistakable without relying on hue.
 */
export default function NetMoneyChart({ netMoney, selectedMonth }: NetMoneyChartProps) {
  /**
   * Drop trailing months where nothing has been recorded yet. A month in progress
   * has a full plan but zero actuals, so plotting it literally draws a cliff to
   * zero that reads as "saved nothing" instead of "not filled in yet".
   *
   * Interior zero months are kept — a zero between two recorded months really is
   * breaking even.
   */
  const rows = useMemo(() => {
    let end = netMoney.length;
    while (end > 0) {
      const point = netMoney[end - 1];
      if (point.income !== 0 || point.expenses !== 0) break;
      end -= 1;
    }
    return netMoney.slice(0, end);
  }, [netMoney]);

  const trimmed = netMoney.length - rows.length;

  if (rows.length === 0) {
    return (
      <p className="text-sm text-gray-400 py-8 text-center">
        No actuals recorded yet — fill in a month to start the trend.
      </p>
    );
  }

  // only marked if the selected month survived the trim
  const selectedPoint = rows.find((point) => point.month === selectedMonth);

  return (
    <>
      <ResponsiveContainer width="100%" height={260}>
        <LineChart data={rows} margin={{ top: 8, right: 24, bottom: 4, left: 4 }}>
          <CartesianGrid vertical={false} stroke="#f1f5f9" />
          <XAxis
            dataKey="month"
            tickFormatter={formatMonthShort}
            tick={{ fill: '#94a3b8', fontSize: 11 }}
            axisLine={false}
            tickLine={false}
            interval="preserveStartEnd"
            minTickGap={24}
          />
          <YAxis
            tickFormatter={
              // wrapped because recharts passes (value, index), and that index
              // would land on formatAxisCurrency's currency parameter
              (value: number) => formatAxisCurrency(value)
            }
            tick={{ fill: '#94a3b8', fontSize: 11 }}
            axisLine={false}
            tickLine={false}
          />
          <Tooltip content={<NetTooltip />} />

          {/* zero is the whole point of this chart: above it saved, below it lost */}
          <ReferenceLine y={0} stroke="#cbd5e1" strokeDasharray="4 4" />

          <Line
            type="monotone"
            dataKey="net"
            stroke={LINE_NET}
            strokeWidth={2}
            dot={{ r: 4, fill: '#ffffff', stroke: LINE_NET, strokeWidth: 2 }}
            activeDot={{ r: 6 }}
          />

          {/* "you are here" — the month the rest of the dashboard is showing */}
          {selectedPoint && (
            <ReferenceDot
              x={selectedPoint.month}
              y={selectedPoint.net}
              r={6}
              fill={LINE_NET}
              stroke="#ffffff"
              strokeWidth={2}
            />
          )}
        </LineChart>
      </ResponsiveContainer>

      {rows.length === 1 && (
        <p className="text-xs text-gray-400 text-center">
          One month recorded — the trend line appears from two months on.
        </p>
      )}
      {trimmed > 0 && (
        <p className="text-xs text-gray-400 text-center">
          {trimmed === 1 ? '1 month is' : `${trimmed} months are`} not shown yet — no actuals
          recorded.
        </p>
      )}
    </>
  );
}
