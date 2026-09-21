package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.CategoryResponse;
import com.networthtracker.backend.entity.Category;
import com.networthtracker.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// business logic for categories.
// categories are a fixed, seeded set: read-only to the application.
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // list every category, already sorted by type then display order
    public List<CategoryResponse> findAll() {
        List<Category> categories = categoryRepository.findAllOrdered();

        List<CategoryResponse> responses = new ArrayList<>();
        for (Category category : categories) {
            responses.add(toResponse(category));
        }
        return responses;
    }

    // convert a Category entity into the safe response DTO
    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setType(category.getType());
        response.setColour(category.getColour());
        response.setFixed(category.isFixed());
        response.setDisplayOrder(category.getDisplayOrder());
        return response;
    }
}
