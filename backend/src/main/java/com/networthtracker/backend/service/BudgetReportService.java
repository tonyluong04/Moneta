package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.BudgetReportResponse;
import com.networthtracker.backend.dto.ExpenseComparison;
import com.networthtracker.backend.dto.MonthTypeTotal;
import com.networthtracker.backend.dto.MonthlyNet;
import com.networthtracker.backend.entity.BudgetEntry;
import com.networthtracker.backend.entity.Category;
import com.networthtracker.backend.entity.CategoryType;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.BudgetEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// assembles the budget dashboard payload:
// the selected month's expenses, plus a continuous net line across all months.
@Service
public class BudgetReportService {

    private final BudgetEntryRepository budgetEntryRepository;

    public BudgetReportService(BudgetEntryRepository budgetEntryRepository) {
        this.budgetEntryRepository = budgetEntryRepository;
    }

    public BudgetReportResponse buildReport(User user, LocalDate month) {
        // squash to the 1st, the same rule the rest of the budget code follows
        LocalDate firstOfMonth = month.withDayOfMonth(1);

        BudgetReportResponse response = new BudgetReportResponse();
        response.setMonth(firstOfMonth);
        response.setExpenses(expensesFor(user, firstOfMonth));
        response.setNetMoney(netMoneyFor(user));
        return response;
    }

    // the selected month's expense rows, in the category order the UI expects
    private List<ExpenseComparison> expensesFor(User user, LocalDate month) {
        List<BudgetEntry> entries = budgetEntryRepository.findByUserAndMonth(user, month);

        List<BudgetEntry> expenseEntries = new ArrayList<>();
        for (BudgetEntry entry : entries) {
            if (entry.getCategory().getType() == CategoryType.EXPENSE) {
                expenseEntries.add(entry);
            }
        }

        // findByUserAndMonth has no ORDER BY, so impose display order here
        expenseEntries.sort(Comparator.comparing(
                (BudgetEntry entry) -> entry.getCategory().getDisplayOrder()));

        List<ExpenseComparison> expenses = new ArrayList<>();
        for (BudgetEntry entry : expenseEntries) {
            Category category = entry.getCategory();
            expenses.add(new ExpenseComparison(
                    category.getId(),
                    category.getName(),
                    entry.getBudgetedAmount(),
                    entry.getActualAmount()));
        }
        return expenses;
    }

    // one point per month from the user's earliest to their latest, gaps filled with zeros
    private List<MonthlyNet> netMoneyFor(User user) {
        List<MonthTypeTotal> totals = budgetEntryRepository.sumActualByMonthAndType(user);
        if (totals.isEmpty()) {
            return new ArrayList<>(); // no data at all — nothing to plot
        }

        // fold the two rows per month (income, expense) into one point each
        Map<LocalDate, MonthlyNet> byMonth = new HashMap<>();
        for (MonthTypeTotal total : totals) {
            MonthlyNet point = byMonth.computeIfAbsent(total.getMonth(), MonthlyNet::new);
            if (total.getType() == CategoryType.INCOME) {
                point.setIncome(total.getTotal());
            } else {
                point.setExpenses(total.getTotal());
            }
        }

        // the query is ordered by month, so the ends of the list are the ends of the range
        LocalDate earliest = totals.get(0).getMonth();
        LocalDate latest = totals.get(totals.size() - 1).getMonth();

        // walk month by month so the line has no invisible jumps. a month with no rows
        // becomes a fresh MonthlyNet, which is already zero income and zero expenses.
        List<MonthlyNet> series = new ArrayList<>();
        LocalDate cursor = earliest;
        while (!cursor.isAfter(latest)) {
            MonthlyNet point = byMonth.get(cursor);
            series.add(point != null ? point : new MonthlyNet(cursor));
            cursor = cursor.plusMonths(1);
        }
        return series;
    }
}
