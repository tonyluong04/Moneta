import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { currentMonth, parseMonthSlug, toMonthParam, toMonthSlug } from '@/lib/month';

/**
 * The shared `?month=YYYY-MM` contract behind the budget table and the dashboard.
 *
 * Both views read and write the month the same way, so navigating between them
 * keeps your place, and any URL can be pasted or bookmarked.
 */
export function useMonthParam() {
  const [searchParams, setSearchParams] = useSearchParams();

  const raw = searchParams.get('month');
  // anything missing or malformed falls back to this month rather than
  // rendering an Invalid Date
  const month = parseMonthSlug(raw) ?? currentMonth();
  const slug = toMonthSlug(month);

  /**
   * Normalise the URL to the month actually on screen. Without this, landing on
   * /budget with no ?month shows the current month but leaves the address bar
   * bare — so copying the link gives someone "whatever month it is for them".
   *
   * replace, not push: correcting the URL shouldn't cost the user a back press.
   * It settles after one pass, because once raw === slug the effect stops firing.
   */
  useEffect(() => {
    if (raw !== slug) {
      setSearchParams({ month: slug }, { replace: true });
    }
  }, [raw, slug, setSearchParams]);

  return {
    /** The selected month, pinned to the 1st. */
    month,
    /** The same month in the API's form, e.g. "2026-10-01". */
    monthParam: toMonthParam(month),
    /** Navigate to another month. Pushes history, so back returns to the previous one. */
    setMonth: (next: Date) => setSearchParams({ month: toMonthSlug(next) }),
  };
}
