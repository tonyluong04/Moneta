/** Mirrors ExpenseComparison — one expense category for the selected month. */
export interface ExpenseComparison {
  categoryId: number;
  name: string;
  budgeted: number;
  actual: number;
}

/** Mirrors MonthlyNet — one point on the money saved/lost line. */
export interface MonthlyNet {
  month: string; // "2026-09-01"
  income: number;
  expenses: number;
  /** income − expenses; positive means saved, negative means lost. */
  net: number;
}

/** Mirrors BudgetReportResponse — everything the dashboard draws, in one payload. */
export interface BudgetReport {
  /** Echoed back by the API so we can tell which month a response belongs to. */
  month: string;
  /** Selected month, expense categories only, in display order. */
  expenses: ExpenseComparison[];
  /** A continuous run of months, oldest first. Months with no data are zero-filled. */
  netMoney: MonthlyNet[];
}
