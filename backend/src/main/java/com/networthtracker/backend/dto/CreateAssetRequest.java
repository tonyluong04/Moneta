package com.networthtracker.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// what the frontend sends to add an asset.
public class CreateAssetRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    // just the id — the service looks up the category and checks you may use it
    @NotNull(message = "Category is required")
    private Integer categoryId;

    /**
     * Required even though the column is nullable. Today every asset is valued by
     * hand, so a missing value is a mistake rather than a state worth storing.
     * Phase 5 relaxes this to "a manual value OR a ticker and a quantity" — a
     * validation change, not a migration, which is why the column stayed permissive.
     */
    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.00", message = "Value cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Value can have at most 2 decimal places")
    private BigDecimal manualValue;

    @Pattern(regexp = "AUD|USD", message = "Currency must be AUD or USD")
    private String currency;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    public CreateAssetRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getManualValue() {
        return manualValue;
    }

    public void setManualValue(BigDecimal manualValue) {
        this.manualValue = manualValue;
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
