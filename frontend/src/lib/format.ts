/**Currency display and amount parsing */
/**
 * PRD §8: always show the currency alongside the value.
 * The 'en-US' locale renders AUD as "A$1,250.00" and USD as "$1,250.00",
 * which matches the PRD examples; 'en-AU' would drop the "A" prefix.
 *
 * `wholeUnits` drops the minor unit for glance views like the portfolio hero
 * ("A$12,001" rather than "A$12,000.50"). Without it each currency keeps its own
 * minor unit, so JPY and VND never show decimals either way.
 *
 * `signDisplay: 'negative'` keeps a zero from printing as "-A$0": an empty
 * liabilities row negated to -0, or a net worth of -0.40 rounded to whole units.
 */
export function formatCurrency(
  value: number,
  currency = 'AUD',
  options: { wholeUnits?: boolean } = {},
): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
    signDisplay: 'negative',
    ...(options.wholeUnits && { minimumFractionDigits: 0, maximumFractionDigits: 0 }),
  }).format(value);
}

/** Turn a text input into a number. Blank or unparseable means "no budget" = 0. */
export function parseAmount(value: string): number {
  const trimmed = value.trim();
  if (trimmed === '') return 0;
  const parsed = Number(trimmed);
  return Number.isFinite(parsed) ? parsed : 0;
}
/**
 * Compact currency for chart axis ticks — no cents, because "A$100" reads
 * instantly on an axis where "A$100.00" is just noise. Tooltips keep the
 * full-precision formatCurrency.
 */
export function formatAxisCurrency(value: number, currency = 'AUD'): string {
  return formatCurrency(value, currency, { wholeUnits: true });
}
