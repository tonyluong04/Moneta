package com.networthtracker.backend.dto;

import java.math.BigDecimal;

// one asset's category total, for one card on the portfolio overview
public class AssetCategoryTotal {
    
    private Integer categoryId;
    private String name;
    private String description; // the card subtitle
    private BigDecimal total;  

    public AssetCategoryTotal(Integer categoryId, String name,
                              String description, BigDecimal total) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.total = total;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
