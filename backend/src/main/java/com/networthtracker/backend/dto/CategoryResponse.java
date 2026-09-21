package com.networthtracker.backend.dto;

import com.networthtracker.backend.entity.CategoryType;

// a category as sent back to the frontend.
// every category is a shared preset, so there is no "preset" flag to report.
public class CategoryResponse {

    private Integer id;
    private String name;
    private CategoryType type;
    private String colour;
    // the amount is the same every month — the actuals form pre-fills it
    private boolean fixed;
    // position within its type; the list arrives already sorted by it
    private Integer displayOrder;

    public CategoryResponse() {}

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
}
