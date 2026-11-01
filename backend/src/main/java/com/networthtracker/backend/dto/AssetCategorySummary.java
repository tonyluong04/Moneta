package com.networthtracker.backend.dto;

// a tiny slice of an asset category, nested inside AssetResponse.
// mirrors CategorySummary, which does the same job for budget rows.
public class AssetCategorySummary {

    private Integer id;
    private String name;

    public AssetCategorySummary() {}

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
}
