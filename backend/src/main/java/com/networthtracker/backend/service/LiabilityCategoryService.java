package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.LiabilityCategoryResponse;
import com.networthtracker.backend.entity.LiabilityCategory;
import com.networthtracker.backend.repository.LiabilityCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic for liability categories.
 *
 * The seven seeded presets, ending in "Other Liability". Read-only — see
 * AssetCategoryService.
 */
@Service
public class LiabilityCategoryService {

    private final LiabilityCategoryRepository liabilityCategoryRepository;

    public LiabilityCategoryService(LiabilityCategoryRepository liabilityCategoryRepository) {
        this.liabilityCategoryRepository = liabilityCategoryRepository;
    }

    // list every category, already sorted by display order
    public List<LiabilityCategoryResponse> findAll() {
        List<LiabilityCategory> categories = liabilityCategoryRepository.findAllOrdered();

        List<LiabilityCategoryResponse> responses = new ArrayList<>();
        for (LiabilityCategory category : categories) {
            responses.add(toResponse(category));
        }
        return responses;
    }

    private LiabilityCategoryResponse toResponse(LiabilityCategory category) {
        LiabilityCategoryResponse response = new LiabilityCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDisplayOrder(category.getDisplayOrder());
        return response;
    }
}
