package com.networthtracker.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// an income/expense category. presets are shared by everyone; custom ones belong to a user.
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The owner of this category. A null user means the category is a preset,
     * shared by every user and not editable by anyone.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    // stored as the text "INCOME"/"EXPENSE", not a number
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType type;

    // hex colour like #16a34a, used for the badge/chart
    @Column(length = 7)
    private String colour;

    // true when the amount is the same every month (rent, insurance, subscriptions).
    // the actuals form pre-fills these from the budgeted amount.
    @Column(name = "is_fixed", nullable = false)
    private boolean fixed;

    // the order this category appears in forms and charts, within its type
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Category() {}

    // stamp created time
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
