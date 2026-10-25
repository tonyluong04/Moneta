package com.networthtracker.backend.dto;

import java.math.BigDecimal;

// one expense category's plan vs outcome for the selected month.
// feeds the budgeted-vs-actual bars (both numbers) and the pie (actual only).
public class ExpenseComparison {

    private Integer categoryId;
    private String name;
    private BigDecimal budgeted;
    private BigDecimal actual;

    public ExpenseComparison() {}

    public ExpenseComparison(Integer categoryId, String name,
                             BigDecimal budgeted, BigDecimal actual) {
        this.categoryId = categoryId;
        this.name = name;
        this.budgeted = budgeted;
        this.actual = actual;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudgeted() {
        return budgeted;
    }

    public void setBudgeted(BigDecimal budgeted) {
        this.budgeted = budgeted;
    }

    public BigDecimal getActual() {
        return actual;
    }

    public void setActual(BigDecimal actual) {
        this.actual = actual;
    }
}
