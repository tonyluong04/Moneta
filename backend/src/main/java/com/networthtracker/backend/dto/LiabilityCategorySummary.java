package com.networthtracker.backend.dto;

// a tiny slice of a liability category, nested inside LiabilityResponse.
public class LiabilityCategorySummary {

    private Integer id;
    private String name;

    public LiabilityCategorySummary() {}

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
