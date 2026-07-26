package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.CategoryResponse;
import com.networthtracker.backend.dto.CreateCategoryRequest;
import com.networthtracker.backend.entity.Category;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// business logic for categories.
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // list every category the user can see (presets + their own)
    public List<CategoryResponse> findAllForUser(User user) {
        List<Category> categories = categoryRepository.findAllVisibleTo(user);

        List<CategoryResponse> responses = new ArrayList<>();
        for (Category category : categories) {
            responses.add(toResponse(category));
        }
        return responses;
    }

    // add a new custom category for the user
    @Transactional
    public CategoryResponse create(User user, CreateCategoryRequest request) {
        String name = request.getName().trim();

        // block duplicates of the same name + type
        if (categoryRepository.existsByUserAndNameIgnoreCaseAndType(user, name, request.getType())) {
            throw new IllegalArgumentException("You already have a category with that name");
        }

        Category category = new Category();
        category.setUser(user); // set owner from the JWT, making it a custom (not preset) category
        category.setName(name);
        category.setType(request.getType());
        category.setColour(request.getColour());

        category = categoryRepository.save(category);
        return toResponse(category);
    }

    // delete one of the user's own categories
    @Transactional
    public void delete(User user, Integer id) {
        // Scoped to the user, so presets and other users' categories are never found.
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        categoryRepository.delete(category);
    }

    // convert a Category entity into the safe response DTO
    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setType(category.getType());
        response.setColour(category.getColour());
        response.setPreset(category.isPreset());
        return response;
    }
}
