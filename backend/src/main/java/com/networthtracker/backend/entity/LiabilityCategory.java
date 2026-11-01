package com.networthtracker.backend.entity;

/**
 * The seven liability categories (PRD 4.3.5).
 *
 * No description field: liabilities render as a plain list rather than the
 * subtitled cards the asset categories get.
 */
public enum LiabilityCategory {

    MORTGAGE("Mortgage (PPOR)", 1),
    INVESTMENT_PROPERTY_LOAN("Investment Property Loan", 2),
    CAR_LOAN("Car Loan", 3),
    PERSONAL_LOAN("Personal Loan", 4),
    CREDIT_CARD("Credit Card Debt", 5),
    STUDENT_DEBT("Student Debt (HECS/HELP)", 6),
    OTHER_LIABILITY("Other Liability", 7);

    private final String label;
    private final int displayOrder;

    LiabilityCategory(String label, int displayOrder) {
        this.label = label;
        this.displayOrder = displayOrder;
    }

    public String getLabel() {
        return label;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
