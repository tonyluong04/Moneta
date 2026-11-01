package com.networthtracker.backend.dto;

import com.networthtracker.backend.entity.LiabilityCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// what the frontend sends to add a liability.
public class CreateLiabilityRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Category is required")
    private LiabilityCategory category;

    /**
     * Amount owing, sent positive. A A$410,000 mortgage is 410000.00, not -410000:
     * the minus sign is applied once, where net worth is calculated and where the
     * figure is rendered. Keeping it out of the payload is what lets the column
     * carry CHECK (balance >= 0) and stop a stray sign turning a debt into an asset.
     */
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Balance can have at most 2 decimal places")
    private BigDecimal balance;

    // optional annual percentage. DECIMAL(5,2) in the table, so 999.99 is the ceiling
    @DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    @Digits(integer = 3, fraction = 2, message = "Interest rate can have at most 2 decimal places")
    private BigDecimal interestRate;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code such as AUD")
    private String currency;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    public CreateLiabilityRequest() {}

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
