package com.networthtracker.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * An asset as sent back to the frontend.
 *
 * The Phase 5 pricing columns (ticker, quantity, live price...) are deliberately
 * absent: the API should not advertise what it cannot do yet.
 *
 * Note the two money fields. `value` is what the asset is worth, and is what every
 * display and total reads; `manualValue` exists only to pre-fill the edit form.
 * Today they are equal, but in Phase 5 `value` becomes quantity x live price for
 * shares and crypto — and because the frontend already reads `value`, that change
 * touches no frontend code at all.
 */
public class AssetResponse {

    private Integer id;
    private String name;
    private AssetCategorySummary category; // nested id + name, like BudgetEntryResponse
    private String currency;
    private BigDecimal manualValue;
    private BigDecimal value;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssetResponse() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetCategorySummary getCategory() {
        return category;
    }

    public void setCategory(AssetCategorySummary category) {
        this.category = category;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getManualValue() {
        return manualValue;
    }

    public void setManualValue(BigDecimal manualValue) {
        this.manualValue = manualValue;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
