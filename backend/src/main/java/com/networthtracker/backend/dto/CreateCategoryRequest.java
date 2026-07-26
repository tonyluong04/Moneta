package com.networthtracker.backend.dto;

import com.networthtracker.backend.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// what the frontend sends to create a custom category.
public class CreateCategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Type is required")
    private CategoryType type;

    // optional hex colour; rejected if not in #rrggbb form
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Colour must be a hex code such as #16a34a")
    private String colour;

    public CreateCategoryRequest() {}

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
}
