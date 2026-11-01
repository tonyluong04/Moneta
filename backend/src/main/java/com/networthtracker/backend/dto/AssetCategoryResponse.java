package com.networthtracker.backend.dto;

import com.networthtracker.backend.entity.PriceSource;

// a full asset category, as served by GET /assets/categories.
// every category is a shared preset, so there is no "preset" flag to report.
public class AssetCategoryResponse {

    private Integer id;
    private String name;
    private String description;
    private Integer displayOrder;
    private PriceSource priceSource; // null means valued by hand

    public AssetCategoryResponse() {}

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
}
