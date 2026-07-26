package com.networthtracker.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

// what the frontend sends to create a budget row.
public class CreateBudgetEntryRequest {

    // which category (just the id — the backend looks up the real category)
    @NotNull(message = "Category is required")
    private Integer categoryId;

    // any date in the month; the service snaps it to the 1st
    @NotNull(message = "Month is required")
    private LocalDate month;

    @NotNull(message = "Budgeted amount is required")
    @DecimalMin(value = "0.00", message = "Budgeted amount cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimal places")
    private BigDecimal budgetedAmount;

    // optional — usually not set yet at setup time (defaults to 0)
    @DecimalMin(value = "0.00", message = "Actual amount cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimal places")
    private BigDecimal actualAmount;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code such as AUD")
    private String currency;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    public CreateBudgetEntryRequest() {}

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

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
