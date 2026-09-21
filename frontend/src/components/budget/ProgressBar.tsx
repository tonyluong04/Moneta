import { cn } from '@/lib/utils';

interface ProgressBarProps {
  actual: number;
  budgeted: number;
}

/**
 * actual ÷ budgeted as a bar plus a percentage.
 * Green while at or under budget, red once over. The bar caps at full width
 * so the layout never breaks, but the number keeps telling the truth (145%).
 */
export default function ProgressBar({ actual, budgeted }: ProgressBarProps) {
  const hasBudget = budgeted > 0;
  const percent = hasBudget ? Math.round((actual / budgeted) * 100) : 0;
  const over = percent > 100;

  return (
    <div className="flex items-center gap-2">
      <div className="h-2 flex-1 rounded-full bg-gray-100 overflow-hidden">
        <div
          className={cn('h-full rounded-full transition-all', over ? 'bg-red-500' : 'bg-emerald-500')}
          style={{ width: `${Math.min(percent, 100)}%` }}
        />
      </div>
      <span
        className={cn(
          'w-12 text-right text-xs tabular-nums',
          !hasBudget && 'text-gray-400',
          hasBudget && over && 'text-red-600 font-medium',
          hasBudget && !over && 'text-gray-500',
        )}
      >
        {hasBudget ? `${percent}%` : '—'}
      </span>
    </div>
  );
}
