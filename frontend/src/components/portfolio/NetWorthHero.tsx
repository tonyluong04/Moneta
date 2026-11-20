import { formatCurrency } from '@/lib/format';
import { cn } from '@/lib/utils';
import type { PortfolioSummary } from '@/types/portfolio';

interface HeroRowProps {
  label: string;
  /** Signed: liabilities arrive here negative. */
  value: number;
  /** The largest absolute value across the three rows, so they share one scale. */
  maxMagnitude: number;
  currency: string;
  headline?: boolean;
}

/**
 * A label, a diverging bar and a value. Zero is the centre tick: positive values
 * fill to the right, negative to the left, so a negative net worth shows as one
 * rather than as an empty bar.
 */
function HeroRow({ label, value, maxMagnitude, currency, headline = false }: HeroRowProps) {
  // zero guard: an empty portfolio has nothing to scale against
  const halfWidthPercent = maxMagnitude > 0 ? (Math.abs(value) / maxMagnitude) * 50 : 0;

  return (
    <div className="grid grid-cols-[6rem_1fr_7rem] sm:grid-cols-[8rem_1fr_9rem] items-center gap-4">
      <span className={cn('text-sm', headline ? 'font-semibold' : 'text-teal-50')}>{label}</span>

      <div className="relative h-2 rounded-full bg-white/20">
        <div
          className={cn('absolute inset-y-0 bg-white/80', value >= 0 ? 'left-1/2 rounded-r-full' : 'right-1/2 rounded-l-full')}
          style={{ width: `${halfWidthPercent}%` }}
        />
        <span className="absolute left-1/2 -top-1 -bottom-1 w-px bg-white/70" />
      </div>

      <span className={cn('text-right tabular-nums', headline ? 'text-xl font-semibold' : 'text-base')}>
        {formatCurrency(value, currency, { wholeUnits: true })}
      </span>
    </div>
  );
}

interface NetWorthHeroProps {
  summary: PortfolioSummary;
}

/**
 * The teal hero from PRD §4.3.1: Assets, Liabilities (shown negative), then Net
 * worth below a dotted rule as the headline. A rounded banner inside the page
 * container, not full-bleed — the shell owns the page width.
 */
export default function NetWorthHero({ summary }: NetWorthHeroProps) {
  const { currency, totalAssets, totalLiabilities, netWorth } = summary;
  const maxMagnitude = Math.max(Math.abs(totalAssets), Math.abs(totalLiabilities), Math.abs(netWorth));

  return (
    <section className="rounded-2xl bg-linear-to-br from-teal-700 to-teal-500 px-10 py-10 text-white">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-light">Portfolio</h1>
        <p className="mt-1 text-sm text-teal-50">A complete view of your wealth</p>
      </div>

      <div className="mx-auto max-w-xl space-y-4">
        <HeroRow label="Assets" value={totalAssets} maxMagnitude={maxMagnitude} currency={currency} />
        <HeroRow label="Liabilities" value={-totalLiabilities} maxMagnitude={maxMagnitude} currency={currency} />
        <div className="border-t border-dashed border-white/40 pt-4">
          <HeroRow label="Net worth" value={netWorth} maxMagnitude={maxMagnitude} currency={currency} headline />
        </div>
      </div>
    </section>
  );
}
