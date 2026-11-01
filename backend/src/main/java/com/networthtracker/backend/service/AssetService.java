package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.AssetCategorySummary;
import com.networthtracker.backend.dto.AssetResponse;
import com.networthtracker.backend.dto.CreateAssetRequest;
import com.networthtracker.backend.dto.UpdateAssetRequest;
import com.networthtracker.backend.entity.Asset;
import com.networthtracker.backend.entity.AssetCategory;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.AssetCategoryRepository;
import com.networthtracker.backend.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetCategoryRepository assetCategoryRepository;

    public AssetService(AssetRepository assetRepository,
                        AssetCategoryRepository assetCategoryRepository) {
        this.assetRepository = assetRepository;
        this.assetCategoryRepository = assetCategoryRepository;
    }

    /** Every asset the user owns, in card order — ordered by the query, not here. */
    public List<AssetResponse> findAll(User user) {
        List<AssetResponse> responses = new ArrayList<>();
        for (Asset asset : assetRepository.findByUserOrdered(user)) {
            responses.add(toResponse(asset));
        }
        return responses;
    }

    /** One asset, by id — only if it belongs to this user. */
    public AssetResponse findOne(User user, Integer id) {
        return toResponse(requireOwn(user, id));
    }

    @Transactional
    public AssetResponse create(User user, CreateAssetRequest request) {
        Asset asset = new Asset();
        asset.setUser(user);
        asset.setName(request.getName().trim());
        asset.setCategory(resolveCategory(user, request.getCategoryId()));
        asset.setManualValue(request.getManualValue());
        if (request.getCurrency() != null) {
            asset.setCurrency(request.getCurrency());
        }
        asset.setNotes(request.getNotes());

        asset = assetRepository.save(asset);
        return toResponse(asset);
    }

    /** Partial update: only the fields actually sent are applied. */
    @Transactional
    public AssetResponse update(User user, Integer id, UpdateAssetRequest request) {
        Asset asset = requireOwn(user, id);

        if (request.getName() != null) {
            asset.setName(request.getName().trim());
        }
        if (request.getCategoryId() != null) {
            asset.setCategory(resolveCategory(user, request.getCategoryId()));
        }
        if (request.getManualValue() != null) {
            asset.setManualValue(request.getManualValue());
        }
        if (request.getCurrency() != null) {
            asset.setCurrency(request.getCurrency());
        }
        if (request.getNotes() != null) {
            asset.setNotes(request.getNotes());
        }

        asset = assetRepository.save(asset);
        return toResponse(asset);
    }

    @Transactional
    public void delete(User user, Integer id) {
        assetRepository.delete(requireOwn(user, id));
    }

    /**
     * Turn an incoming categoryId into a category, and check the user may use it:
     * a preset, or one of their own. Someone else's custom category is not found.
     */
    private AssetCategory resolveCategory(User user, Integer categoryId) {
        return assetCategoryRepository.findVisibleById(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    /**
     * Load by id AND user, so another account's row is simply "not found" rather
     * than forbidden — the same guard the budget rows use.
     */
    private Asset requireOwn(User user, Integer id) {
        return assetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
    }

    private AssetResponse toResponse(Asset asset) {
        AssetCategory category = asset.getCategory();

        AssetCategorySummary categorySummary = new AssetCategorySummary();
        categorySummary.setId(category.getId());
        categorySummary.setName(category.getName());

        AssetResponse response = new AssetResponse();
        response.setId(asset.getId());
        response.setName(asset.getName());
        response.setCategory(categorySummary);
        response.setCurrency(asset.getCurrency());
        response.setManualValue(asset.getManualValue());
        // the Phase 5 seam: ask the entity what it is worth, never read manualValue here
        response.setValue(asset.getValue());
        response.setNotes(asset.getNotes());
        response.setCreatedAt(asset.getCreatedAt());
        response.setUpdatedAt(asset.getUpdatedAt());
        return response;
    }
}
