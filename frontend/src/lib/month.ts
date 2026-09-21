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
/** URL form for the ?month= query param, e.g. "2026-03". */
export function toMonthSlug(month: Date): string {
  const year = month.getFullYear();
  const paddedMonth = String(month.getMonth() + 1).padStart(2, '0');
  return `${year}-${paddedMonth}`;
}

/**
 * Parse "2026-03" back into a Date pinned to the 1st.
 * Returns null for anything malformed, so the page can fall back to this month
 * rather than rendering an Invalid Date.
 */
export function parseMonthSlug(slug: string | null): Date | null {
  if (!slug) return null;

  const match = /^(\d{4})-(\d{2})$/.exec(slug.trim());
  if (!match) return null;

  const year = Number(match[1]);
  const monthIndex = Number(match[2]) - 1;
  if (monthIndex < 0 || monthIndex > 11) return null;

  return new Date(year, monthIndex, 1);
}

/** True when the given month is the one we are currently living in. */
export function isCurrentMonth(month: Date): boolean {
  const now = new Date();
  return month.getFullYear() === now.getFullYear() && month.getMonth() === now.getMonth();
}

/**
 * How far through the month we are, for the mid-month pace indicator.
 * Day 0 of the next month is the last day of this one, which is how we get
 * the length of the month without a lookup table.
 */
export function monthElapsed(month: Date): { day: number; daysInMonth: number; percent: number } {
  const daysInMonth = new Date(month.getFullYear(), month.getMonth() + 1, 0).getDate();
  const day = new Date().getDate();
  return { day, daysInMonth, percent: Math.round((day / daysInMonth) * 100) };
}
