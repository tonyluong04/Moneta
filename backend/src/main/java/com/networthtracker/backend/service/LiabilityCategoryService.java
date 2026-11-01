package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.CreateLiabilityCategoryRequest;
import com.networthtracker.backend.dto.LiabilityCategoryResponse;
import com.networthtracker.backend.entity.LiabilityCategory;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.LiabilityCategoryRepository;
import com.networthtracker.backend.repository.LiabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/** Liability categories: the seven presets, plus the ones a user adds. */
@Service
public class LiabilityCategoryService {

    private final LiabilityCategoryRepository liabilityCategoryRepository;
    private final LiabilityRepository liabilityRepository;

    public LiabilityCategoryService(LiabilityCategoryRepository liabilityCategoryRepository,
                                    LiabilityRepository liabilityRepository) {
        this.liabilityCategoryRepository = liabilityCategoryRepository;
        this.liabilityRepository = liabilityRepository;
    }

    public List<LiabilityCategoryResponse> findVisible(User user) {
        List<LiabilityCategoryResponse> responses = new ArrayList<>();
        for (LiabilityCategory category : liabilityCategoryRepository.findVisibleTo(user)) {
            responses.add(toResponse(category));
        }
        return responses;
    }

    @Transactional
    public LiabilityCategoryResponse create(User user, CreateLiabilityCategoryRequest request) {
        String name = request.getName().trim();

        if (liabilityCategoryRepository.existsByUserAndNameIgnoreCase(user, name)) {
            throw new IllegalArgumentException("You already have a category called " + name);
        }

        LiabilityCategory category = new LiabilityCategory();
        category.setUser(user);
        category.setName(name);
        category.setDisplayOrder(liabilityCategoryRepository.maxDisplayOrderFor(user) + 1);

        category = liabilityCategoryRepository.save(category);
        return toResponse(category);
    }

    /** Same two guards as AssetCategoryService.delete. */
    @Transactional
    public void delete(User user, Integer id) {
        LiabilityCategory category = liabilityCategoryRepository.findVisibleById(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.isPreset()) {
            throw new IllegalArgumentException("Preset categories cannot be deleted");
        }

        long inUse = liabilityRepository.countByCategory(category);
        if (inUse > 0) {
            throw new IllegalArgumentException("That category still holds " + inUse
                    + (inUse == 1 ? " liability" : " liabilities")
                    + " — move or delete them first");
        }

        liabilityCategoryRepository.delete(category);
    }

    private LiabilityCategoryResponse toResponse(LiabilityCategory category) {
        LiabilityCategoryResponse response = new LiabilityCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setPreset(category.isPreset());
        return response;
    }
}
