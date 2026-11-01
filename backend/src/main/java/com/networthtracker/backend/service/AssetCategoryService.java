package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.AssetCategoryResponse;
import com.networthtracker.backend.dto.CreateAssetCategoryRequest;
import com.networthtracker.backend.entity.AssetCategory;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.AssetCategoryRepository;
import com.networthtracker.backend.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Asset categories: the eight presets, plus the ones a user adds.
 *
 * Budget categories live in a table too but are read-only (task 2.8). Same storage
 * mechanism, different policy — and the policy lives here rather than in the schema.
 */
@Service
public class AssetCategoryService {

    private final AssetCategoryRepository assetCategoryRepository;
    private final AssetRepository assetRepository;

    public AssetCategoryService(AssetCategoryRepository assetCategoryRepository,
                                AssetRepository assetRepository) {
        this.assetCategoryRepository = assetCategoryRepository;
        this.assetRepository = assetRepository;
    }

    /** Presets plus this user's own, in display order. */
    public List<AssetCategoryResponse> findVisible(User user) {
        List<AssetCategoryResponse> responses = new ArrayList<>();
        for (AssetCategory category : assetCategoryRepository.findVisibleTo(user)) {
            responses.add(toResponse(category));
        }
        return responses;
    }

    @Transactional
    public AssetCategoryResponse create(User user, CreateAssetCategoryRequest request) {
        String name = request.getName().trim();

        if (assetCategoryRepository.existsByUserAndNameIgnoreCase(user, name)) {
            throw new IllegalArgumentException("You already have a category called " + name);
        }

        AssetCategory category = new AssetCategory();
        category.setUser(user);
        category.setName(name);
        category.setDescription(request.getDescription());
        // sorts after everything already visible, so the presets keep positions 1-8
        category.setDisplayOrder(assetCategoryRepository.maxDisplayOrderFor(user) + 1);
        // priceSource stays null: nothing can live-price a user-defined category

        category = assetCategoryRepository.save(category);
        return toResponse(category);
    }

    /**
     * Delete one of your own categories.
     *
     * Two guards: a preset belongs to no one and is never deletable, and a category
     * still holding assets is refused rather than cascading those assets away or
     * silently reassigning them.
     */
    @Transactional
    public void delete(User user, Integer id) {
        AssetCategory category = assetCategoryRepository.findVisibleById(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.isPreset()) {
            throw new IllegalArgumentException("Preset categories cannot be deleted");
        }

        long inUse = assetRepository.countByCategory(category);
        if (inUse > 0) {
            throw new IllegalArgumentException("That category still holds " + inUse
                    + (inUse == 1 ? " asset" : " assets") + " — move or delete them first");
        }

        assetCategoryRepository.delete(category);
    }

    private AssetCategoryResponse toResponse(AssetCategory category) {
        AssetCategoryResponse response = new AssetCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setPriceSource(category.getPriceSource());
        response.setPreset(category.isPreset());
        return response;
    }
}
