package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.BudgetReportResponse;
import com.networthtracker.backend.dto.ExpenseComparison;
import com.networthtracker.backend.dto.MonthlyNet;
import com.networthtracker.backend.entity.BudgetEntry;
import com.networthtracker.backend.entity.Category;
import com.networthtracker.backend.entity.CategoryType;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.BudgetEntryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// assembles the budget dashboard payload:
// the selected month's expenses, plus a continuous net line across all months.
// every amount is in the user's preferred currency, converted at the latest rates —
// for past months too, so the whole dashboard reads at one set of rates.
@Service
public class BudgetReportService {

    private final BudgetEntryRepository budgetEntryRepository;
    private final CurrencyConverter currencyConverter;

    public BudgetReportService(BudgetEntryRepository budgetEntryRepository,
                               CurrencyConverter currencyConverter) {
        this.budgetEntryRepository = budgetEntryRepository;
        this.currencyConverter = currencyConverter;
    }

    public BudgetReportResponse buildReport(User user, LocalDate month) {
        // squash to the 1st, the same rule the rest of the budget code follows
        LocalDate firstOfMonth = month.withDayOfMonth(1);

        BudgetReportResponse response = new BudgetReportResponse();
        String target = user.getPreferredCurrency();
        response.setMonth(firstOfMonth);
        response.setCurrency(target);
        response.setExpenses(expensesFor(user, firstOfMonth, target));
        response.setNetMoney(netMoneyFor(user, target));
        return response;
    }

    // the selected month's expense rows, in the category order the UI expects
    private List<ExpenseComparison> expensesFor(User user, LocalDate month, String target) {
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
            // one row per category per month, so each figure is final: convert and round it
            expenses.add(new ExpenseComparison(
                    category.getId(),
                    category.getName(),
                    currencyConverter.convertAndRound(entry.getBudgetedAmount(), entry.getCurrency(), target),
                    currencyConverter.convertAndRound(entry.getActualAmount(), entry.getCurrency(), target)));
        }
        return expenses;
    }

    // one point per month from the user's earliest to their latest, gaps filled with zeros
    private List<MonthlyNet> netMoneyFor(User user, String target) {
        List<BudgetEntry> entries = budgetEntryRepository.findByUserWithCategoryOrderByMonth(user);
        if (entries.isEmpty()) {
            return new ArrayList<>(); // no data at all — nothing to plot
        }

        // convert every row's actual before adding it to its month's income or expenses;
        // the sums stay unrounded until each month is complete
        Map<LocalDate, MonthlyNet> byMonth = new HashMap<>();
        for (BudgetEntry entry : entries) {
            MonthlyNet point = byMonth.computeIfAbsent(entry.getMonth(), MonthlyNet::new);
            BigDecimal actual = currencyConverter.convert(entry.getActualAmount(), entry.getCurrency(), target);
            if (entry.getCategory().getType() == CategoryType.INCOME) {
                point.setIncome(point.getIncome().add(actual));
            } else {
                point.setExpenses(point.getExpenses().add(actual));
            }
        }

        // round each month once, now that its sums are final — net from the unrounded
        // sums too, so it never drifts a cent from income minus expenses as converted
        for (MonthlyNet point : byMonth.values()) {
            BigDecimal income = point.getIncome();
            BigDecimal expenses = point.getExpenses();
            point.setIncome(currencyConverter.round(income, target));
            point.setExpenses(currencyConverter.round(expenses, target));
            point.setNet(currencyConverter.round(income.subtract(expenses), target));
        }

        // the query is ordered by month, so the ends of the list are the ends of the range
        LocalDate earliest = entries.get(0).getMonth();
        LocalDate latest = entries.get(entries.size() - 1).getMonth();

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
