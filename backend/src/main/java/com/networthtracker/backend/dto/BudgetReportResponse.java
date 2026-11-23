package com.networthtracker.backend.dto;

import java.time.LocalDate;
import java.util.List;

// everything the budget dashboard draws, in one request.
public class BudgetReportResponse {

    // echoed back so the client can tell which month a response belongs to
    private LocalDate month;

    // the user's preferred currency; every amount below is converted into it
    private String currency;

    // selected month, expense categories only — bar chart and pie
    private List<ExpenseComparison> expenses;

    // continuous run of months, oldest first — the net line
    private List<MonthlyNet> netMoney;

    public BudgetReportResponse() {}

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<ExpenseComparison> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseComparison> expenses) {
        this.expenses = expenses;
    }

    public List<MonthlyNet> getNetMoney() {
        return netMoney;
    }

    public void setNetMoney(List<MonthlyNet> netMoney) {
        this.netMoney = netMoney;
    }
}
