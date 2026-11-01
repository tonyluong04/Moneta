package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.AssetResponse;
import com.networthtracker.backend.dto.CategoryOption;
import com.networthtracker.backend.dto.CreateAssetRequest;
import com.networthtracker.backend.dto.UpdateAssetRequest;
import com.networthtracker.backend.entity.Asset;
import com.networthtracker.backend.entity.AssetCategory;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    /** The eight categories, for the add form and the overview cards. */
    public List<CategoryOption> findCategories() {
        List<CategoryOption> options = new ArrayList<>();
        for (AssetCategory category : AssetCategory.values()) {
            CategoryOption option = new CategoryOption();
            option.setValue(category.name());
            option.setLabel(category.getLabel());
            option.setDescription(category.getDescription());
            option.setDisplayOrder(category.getDisplayOrder());
            options.add(option);
        }
        return options;
    }

    /** Every asset the user owns, in card order. */
    public List<AssetResponse> findAll(User user) {
        List<Asset> assets = assetRepository.findByUserOrderByCategoryAscNameAsc(user);

        // The database can only order the category column alphabetically ("CASH",
        // "CRYPTO", "INVESTMENT_PROPERTY"...), which is not the order the cards want.
        // displayOrder lives on the enum, so the final sort has to happen here.
        // List.sort is stable, so the name ordering the query produced survives
        // inside each category and does not need restating.
        // the (Asset asset) type is spelled out because List.sort takes a
        // Comparator<? super Asset>, and that wildcard defeats inference
        assets.sort(Comparator.comparingInt((Asset asset) -> asset.getCategory().getDisplayOrder()));

        List<AssetResponse> responses = new ArrayList<>();
        for (Asset asset : assets) {
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
        asset.setCategory(request.getCategory());
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
        if (request.getCategory() != null) {
            asset.setCategory(request.getCategory());
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
     * Load by id AND user, so another account's row is simply "not found" rather
     * than forbidden — the same guard the budget rows use.
     */
    private Asset requireOwn(User user, Integer id) {
        return assetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
    }

    private AssetResponse toResponse(Asset asset) {
        AssetResponse response = new AssetResponse();
        response.setId(asset.getId());
        response.setName(asset.getName());
        response.setCategory(asset.getCategory());
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
