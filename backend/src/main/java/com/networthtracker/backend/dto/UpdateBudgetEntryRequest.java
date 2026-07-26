package com.networthtracker.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// editing a budget row — no categoryId/month (those are its identity, can't change).
// every field optional: only the ones sent are applied (mainly for entering actuals).
public class UpdateBudgetEntryRequest {

    @DecimalMin(value = "0.00", message = "Budgeted amount cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimal places")
    private BigDecimal budgetedAmount;

    @DecimalMin(value = "0.00", message = "Actual amount cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimal places")
    private BigDecimal actualAmount;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code such as AUD")
    private String currency;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    public UpdateBudgetEntryRequest() {}

    public BigDecimal getBudgetedAmount() {
        return budgetedAmount;
    }

    public void setBudgetedAmount(BigDecimal budgetedAmount) {
        this.budgetedAmount = budgetedAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
