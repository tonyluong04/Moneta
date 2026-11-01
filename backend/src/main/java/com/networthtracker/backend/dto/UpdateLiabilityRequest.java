package com.networthtracker.backend.dto;

import com.networthtracker.backend.entity.LiabilityCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// editing a liability. every field optional — only the ones sent are applied,
// which is how "just paid down the mortgage" is a one-field PUT of the balance.
public class UpdateLiabilityRequest {

    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    private LiabilityCategory category;

    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Balance can have at most 2 decimal places")
    private BigDecimal balance;

    @DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    @Digits(integer = 3, fraction = 2, message = "Interest rate can have at most 2 decimal places")
    private BigDecimal interestRate;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code such as AUD")
    private String currency;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    public UpdateLiabilityRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LiabilityCategory getCategory() {
        return category;
    }

    public void setCategory(LiabilityCategory category) {
        this.category = category;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
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
