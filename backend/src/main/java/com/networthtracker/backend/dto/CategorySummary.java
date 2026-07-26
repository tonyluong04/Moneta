package com.networthtracker.backend.dto;

// a tiny, safe slice of a category (id, name, colour) — nested inside other responses.
public class CategorySummary {

    private Integer id;
    private String name;
    private String colour;

    public CategorySummary() {}

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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
