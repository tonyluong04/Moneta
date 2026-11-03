import { toMonthSlug } from '@/lib/month';

/**
 * Every in-app URL, built in one place.
 *
 * Pages used to assemble `/budget?month=${toMonthSlug(month)}` by hand, so the
 * `?month=YYYY-MM` contract lived in several files. Now a link is
 * `paths.budget(month)`, and renaming a route is a one-line change here plus
 * the matching entry in router.tsx.
 */
export const paths = {
  login: '/login',
  register: '/register',
  portfolio: '/portfolio',

  /** The budget table, optionally pinned to a month. */
  budget: (month?: Date) => (month ? `/budget?month=${toMonthSlug(month)}` : '/budget'),

  /** The read-only charts, optionally pinned to a month. */
  budgetDashboard: (month?: Date) =>
    month ? `/budget/dashboard?month=${toMonthSlug(month)}` : '/budget/dashboard',
};
