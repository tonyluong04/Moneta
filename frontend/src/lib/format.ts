/**Currency display and amount parsing */
/**
 * PRD §8: always show the currency alongside the value.
 * The 'en-US' locale renders AUD as "A$1,250.00" and USD as "$1,250.00",
 * which matches the PRD examples; 'en-AU' would drop the "A" prefix.
 */
export function formatCurrency(value: number, currency = 'AUD'): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
  }).format(value);
}

/** Turn a text input into a number. Blank or unparseable means "no budget" = 0. */
export function parseAmount(value: string): number {
  const trimmed = value.trim();
  if (trimmed === '') return 0;
  const parsed = Number(trimmed);
  return Number.isFinite(parsed) ? parsed : 0;
}