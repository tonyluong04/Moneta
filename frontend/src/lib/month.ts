/**Month math and formatting. The page holds the selected month as a Date pinned to the 1st. */
/** The 1st of the current month, in the browser's local timezone. */
export function currentMonth(): Date {
  const now = new Date();
  return new Date(now.getFullYear(), now.getMonth(), 1);
}

/** Move forward/back by whole months. addMonths(july, -1) === june. */
export function addMonths(month: Date, delta: number): Date {
  return new Date(month.getFullYear(), month.getMonth() + delta, 1);
}

/**
 * Format for the API's ?month= param: "YYYY-MM-01".
 * Built from local date parts on purpose — toISOString() converts to UTC,
 * which in AEST (UTC+10) rolls the 1st of the month back into the previous month.
 */
export function toMonthParam(month: Date): string {
  const year = month.getFullYear();
  const paddedMonth = String(month.getMonth() + 1).padStart(2, '0');
  return `${year}-${paddedMonth}-01`;
}

/** Human label for the header, e.g. "September 2026". */
export function formatMonthLabel(month: Date): string {
  return month.toLocaleDateString('en-AU', { month: 'long', year: 'numeric' });
}