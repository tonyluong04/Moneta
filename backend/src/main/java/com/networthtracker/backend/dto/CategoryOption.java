package com.networthtracker.backend.dto;

/**
 * One selectable category, as sent to the frontend dropdowns and cards.
 *
 * Shared by assets and liabilities so there is a single TypeScript type on the
 * other side. Liabilities carry no description, so that field comes back null
 * for them — see LiabilityCategory.
 */
public class CategoryOption {

    private String value;        // the stable enum name, e.g. "CASH" — what POST/PUT send
    private String label;        // what the user reads, e.g. "Cash"
    private String description;  // helper text under the label; null for liabilities
    private int displayOrder;

    public CategoryOption() {}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
