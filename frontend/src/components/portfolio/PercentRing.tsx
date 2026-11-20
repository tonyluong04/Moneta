const SIZE = 64;
const STROKE = 6;
const RADIUS = (SIZE - STROKE) / 2;
const CIRCUMFERENCE = 2 * Math.PI * RADIUS;

interface PercentRingProps {
  /** 0–100, unrounded. The arc uses it as-is; only the label rounds. */
  percent: number;
}

/** Whole percent, but a non-zero share that rounds to 0 reads "<1%", not "0%". */
function percentLabel(percent: number): string {
  if (percent > 0 && Math.round(percent) === 0) return '<1%';
  return `${Math.round(percent)}%`;
}

/**
 * One value against a track — a category's share of total assets.
 *
 * Plain SVG rather than a Recharts radial chart: it is a single arc, and eight
 * cards would otherwise each carry a responsive chart container. One teal for
 * every ring, because nothing here is compared by colour (PRD §4.3.1).
 */
export default function PercentRing({ percent }: PercentRingProps) {
  const clamped = Math.min(Math.max(percent, 0), 100);
  const arcLength = (clamped / 100) * CIRCUMFERENCE;
  const label = percentLabel(clamped);

  return (
    <div className="relative" style={{ width: SIZE, height: SIZE }} role="img" aria-label={`${label} of total assets`}>
      {/* rotated so the arc starts at 12 o'clock */}
      <svg width={SIZE} height={SIZE} className="-rotate-90">
        <circle
          cx={SIZE / 2}
          cy={SIZE / 2}
          r={RADIUS}
          fill="none"
          strokeWidth={STROKE}
          className="stroke-gray-100"
        />
        {/* skipped at zero: a zero-length arc with a round cap still draws a dot */}
        {arcLength > 0 && (
          <circle
            cx={SIZE / 2}
            cy={SIZE / 2}
            r={RADIUS}
            fill="none"
            strokeWidth={STROKE}
            strokeLinecap="round"
            strokeDasharray={`${arcLength} ${CIRCUMFERENCE}`}
            className="stroke-teal-600 transition-all"
          />
        )}
      </svg>
      <span className="absolute inset-0 flex items-center justify-center text-xs font-medium text-gray-700 tabular-nums">
        {label}
      </span>
    </div>
  );
}
