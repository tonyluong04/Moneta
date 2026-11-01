package com.networthtracker.backend.dto;

import com.networthtracker.backend.entity.LiabilityCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A liability as sent back to the frontend.
 *
 * No `value` field, unlike AssetResponse. Liability.getValue() exists so the
 * summary service can total both sides the same way, but for a debt it will always
 * just be the balance — there is no live pricing coming for it. Shipping both
 * would be two names for one number, so the API sends the one the user recognises.
 *
 * `balance` is positive here, as it is in the table. Net worth and the portfolio
 * hero are the only places the sign is applied.
 */
public class LiabilityResponse {

    private Integer id;
    private String name;
    private LiabilityCategory category; // serialises as its name, e.g. "MORTGAGE"
    private String currency;
    private BigDecimal balance;
    private BigDecimal interestRate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LiabilityResponse() {}

    // derived, no backing field — same trick as AssetResponse
    public String getCategoryLabel() {
        return category != null ? category.getLabel() : null;
    }

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

    public LiabilityCategory getCategory() {
        return category;
    }

    public void setCategory(LiabilityCategory category) {
        this.category = category;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
