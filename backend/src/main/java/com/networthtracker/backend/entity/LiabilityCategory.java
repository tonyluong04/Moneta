package com.networthtracker.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * A liability category — seven seeded presets plus whatever the user adds.
 *
 * Same shape as AssetCategory minus two fields: no description, because liabilities
 * render as a plain list rather than subtitled cards, and no price source, because
 * nothing prices a debt from an API.
 */
@Entity
@Table(name = "liability_categories")
public class LiabilityCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // null for a preset shared by everyone; set for a user's own category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public LiabilityCategory() {}

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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
