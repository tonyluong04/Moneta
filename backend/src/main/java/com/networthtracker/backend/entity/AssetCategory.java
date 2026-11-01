package com.networthtracker.backend.entity;

/**
 * The eight asset categories (PRD 4.3.2).
 *
 * Label, description and card order live here rather than in a seeded table:
 * the set only changes when this file changes, so a table plus a migration to
 * reword a subtitle would be ceremony for nothing. The API hands these fields
 * to the frontend so the portfolio cards are never hardcoded there.
 */
public enum AssetCategory {

    CASH("Cash", "Bank accounts, physical cash", 1),
    PROPERTY_PPOR("Property - PPOR", "Your primary home", 2),
    INVESTMENT_PROPERTY("Investment Property", "Rental or investment properties", 3),
    SHARES("Shares", "Stock market investments", 4),
    CRYPTO("Crypto", "Digital currency assets", 5),
    SUPERANNUATION("Super", "Retirement savings accounts", 6),
    MOTOR_VEHICLES("Motor Vehicles", "Cars, bikes, boats, etc.", 7),
    OTHER_ASSETS("Other assets", "Valuables, collectibles", 8);

    private final String label;
    private final String description;
    private final int displayOrder;

    AssetCategory(String label, String description, int displayOrder) {
        this.label = label;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    // true for the two categories Phase 5 will price from an API rather than by hand
    public boolean isLivePriced() {
        return this == SHARES || this == CRYPTO;
    }
}
