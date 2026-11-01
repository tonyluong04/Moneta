package com.networthtracker.backend.dto;

// a full liability category, as served by GET /liabilities/categories.
// no description or price source — see the LiabilityCategory entity.
public class LiabilityCategoryResponse {

    private Integer id;
    private String name;
    private Integer displayOrder;
    private boolean preset;

    public LiabilityCategoryResponse() {}

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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isPreset() {
        return preset;
    }

    public void setPreset(boolean preset) {
        this.preset = preset;
    }
}
