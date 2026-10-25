package com.networthtracker.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// one month on the money saved/lost line.
// a month with no data is this object left untouched — zeros all the way down.
public class MonthlyNet {

    private LocalDate month;
    private BigDecimal income = BigDecimal.ZERO;
    private BigDecimal expenses = BigDecimal.ZERO;

    public MonthlyNet() {}

    public MonthlyNet(LocalDate month) {
        this.month = month;
    }

    // derived, never stored — positive means money saved, negative means lost
    public BigDecimal getNet() {
        return income.subtract(expenses);
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }
}
