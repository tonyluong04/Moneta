package com.networthtracker.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * An asset category — eight seeded presets plus whatever the user adds.
 *
 * Was a java enum in task 4.1. It became a table once categories had to be
 * user-extensible, which is also why display_order is now a real column: the
 * database can order by it, so the services no longer re-sort in java.
 */
@Entity
@Table(name = "asset_categories")
public class AssetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // null for a preset shared by everyone; set for a user's own category
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

    /** A preset belongs to no one, and nobody may delete or edit it. */
    public boolean isPreset() {
        return user == null;
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
