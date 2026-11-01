package com.networthtracker.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// something the user owns. debts live in Liability, not here with a sign flag.
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    // stored as the text "CASH"/"SHARES"/..., not a number
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssetCategory category;

    @Column(nullable = false, length = 3)
    private String currency = "AUD";

    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * What the user says it is worth. Null is meaningful: from Phase 5 a share or
     * coin is valued as quantity x lastLivePrice and leaves this unset, which is
     * how "priced by the API" stays distinguishable from "worth nothing".
     */
    @Column(name = "manual_value", precision = 15, scale = 2)
    private BigDecimal manualValue;

    // ---- Phase 5 (live pricing). Mapped now so the entity is not reopened later. ----

    @Column(name = "ticker_symbol", length = 20)
    private String tickerSymbol;

    @Column(name = "coin_id", length = 50)
    private String coinId;

    @Column(precision = 20, scale = 8)
    private BigDecimal quantity;

    @Column(name = "purchase_price", precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "last_live_price", precision = 15, scale = 2)
    private BigDecimal lastLivePrice;

    @Column(name = "last_price_updated_at")
    private LocalDateTime lastPriceUpdatedAt;

    // ---- end Phase 5 ----

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Asset() {}

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
     * What this asset contributes to the portfolio total.
     *
     * Today that is always the manual value, with null treated as zero. Phase 5
     * extends this to quantity x lastLivePrice for shares and crypto, which is why
     * the rest of the code asks for this rather than reading manualValue directly.
     */
    public BigDecimal getValue() {
        return manualValue != null ? manualValue : BigDecimal.ZERO;
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

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
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

    public BigDecimal getManualValue() {
        return manualValue;
    }

    public void setManualValue(BigDecimal manualValue) {
        this.manualValue = manualValue;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getLastLivePrice() {
        return lastLivePrice;
    }

    public void setLastLivePrice(BigDecimal lastLivePrice) {
        this.lastLivePrice = lastLivePrice;
    }

    public LocalDateTime getLastPriceUpdatedAt() {
        return lastPriceUpdatedAt;
    }

    public void setLastPriceUpdatedAt(LocalDateTime lastPriceUpdatedAt) {
        this.lastPriceUpdatedAt = lastPriceUpdatedAt;
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
