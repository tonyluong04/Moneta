package com.networthtracker.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * An asset category — the eight seeded presets, ending in "Other assets".
 *
 * Was a java enum in task 4.1. It is a table because the metadata belongs in data:
 * price_source decides which Phase 5 provider prices the category rather than a
 * hardcoded name check, and display_order being a real column means the database
 * orders the lists, so the services no longer re-sort in java.
 */
@Entity
@Table(name = "asset_categories")
public class AssetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The owner of this category. Always null today: every category is a preset
     * shared by every user, exactly as with budget categories. The column is here
     * so the three category tables share one shape — see V10.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    // null means valued by hand — see PriceSource
    @Enumerated(EnumType.STRING)
    @Column(name = "price_source", length = 10)
    private PriceSource priceSource;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public AssetCategory() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public PriceSource getPriceSource() {
        return priceSource;
    }

    public void setPriceSource(PriceSource priceSource) {
        this.priceSource = priceSource;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
