package com.networthtracker.backend.dto;

import com.networthtracker.backend.entity.AssetCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// editing an asset. every field optional — only the ones sent are applied.
// unlike a budget row, category here is an ordinary attribute and may change
// (something filed under Other assets, later reclassified as Shares).
public class UpdateAssetRequest {

    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    private AssetCategory category;

    @DecimalMin(value = "0.00", message = "Value cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Value can have at most 2 decimal places")
    private BigDecimal manualValue;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code such as AUD")
    private String currency;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    public UpdateAssetRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
        this.category = category;
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
