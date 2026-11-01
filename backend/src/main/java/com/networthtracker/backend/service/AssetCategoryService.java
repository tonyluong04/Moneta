package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.AssetCategoryResponse;
import com.networthtracker.backend.entity.AssetCategory;
import com.networthtracker.backend.repository.AssetCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic for asset categories.
 *
 * The eight seeded presets, ending in "Other assets" for anything that fits none of
 * the rest. Read-only to the application, exactly like budget categories: all three
 * category sets are tables the app only ever reads.
 */
@Service
public class AssetCategoryService {

    private final AssetCategoryRepository assetCategoryRepository;

    public AssetCategoryService(AssetCategoryRepository assetCategoryRepository) {
        this.assetCategoryRepository = assetCategoryRepository;
    }

    // list every category, already sorted by display order
    public List<AssetCategoryResponse> findAll() {
        List<AssetCategory> categories = assetCategoryRepository.findAllOrdered();

        List<AssetCategoryResponse> responses = new ArrayList<>();
        for (AssetCategory category : categories) {
            responses.add(toResponse(category));
        }
        return responses;
    }

    // convert an AssetCategory entity into the response DTO
    private AssetCategoryResponse toResponse(AssetCategory category) {
        AssetCategoryResponse response = new AssetCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setPriceSource(category.getPriceSource());
        return response;
    }
}
