package com.networthtracker.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// something the user owes. no live pricing and no cost basis — a debt is just a
// balance and a rate, which is why this is its own table rather than a flag on Asset.
@Entity
@Table(name = "liabilities")
public class Liability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    // a preset or one of this user's own — see LiabilityCategory
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liability_category_id")
    private LiabilityCategory category;

    @Column(nullable = false, length = 3)
    private String currency = "AUD";

    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Amount owing, stored positive. The minus sign is a display concern — the
     * portfolio hero renders it negative and net worth subtracts it.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    // annual percentage, optional
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Liability() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * What this liability contributes to the portfolio total, positive.
     * Mirrors Asset.getValue() so the summary service can treat both the same way
     * and apply the sign once, in one place.
     */
    public BigDecimal getValue() {
        return balance != null ? balance : BigDecimal.ZERO;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
