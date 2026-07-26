package com.networthtracker.backend.dto;

import com.networthtracker.backend.entity.CategoryType;

// a category as sent back to the frontend.
public class CategoryResponse {

    private Integer id;
    private String name;
    private CategoryType type;
    private String colour;
    private boolean preset; // tells the UI to disable delete on presets

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

    public boolean isPreset() {
        return preset;
    }

    public void setPreset(boolean preset) {
        this.preset = preset;
    }
}
