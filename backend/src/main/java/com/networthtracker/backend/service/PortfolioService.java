package com.networthtracker.backend.service;

import com.networthtracker.backend.dto.AssetCategoryTotal;
import com.networthtracker.backend.dto.PortfolioSummaryResponse;
import com.networthtracker.backend.entity.Asset;
import com.networthtracker.backend.entity.AssetCategory;
import com.networthtracker.backend.entity.Liability;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.AssetCategoryRepository;
import com.networthtracker.backend.repository.AssetRepository;
import com.networthtracker.backend.repository.LiabilityRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// assembles the portfolio overview payload: the hero totals plus one total per category card.
@Service
public class PortfolioService {

    private final AssetRepository assetRepository;
    private final LiabilityRepository liabilityRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final CurrencyConverter currencyConverter;

    public PortfolioService(AssetRepository assetRepository,
                            LiabilityRepository liabilityRepository,
                            AssetCategoryRepository assetCategoryRepository,
                            CurrencyConverter currencyConverter) {
        this.assetRepository = assetRepository;
        this.liabilityRepository = liabilityRepository;
        this.assetCategoryRepository = assetCategoryRepository;
        this.currencyConverter = currencyConverter;
    }

    /**
     * Both sides are summed here in java, not SQL: an asset's worth comes from
     * getValue(), which from Phase 5 is derived (quantity x live price) and is not a
     * column SQL could add up. Liabilities go through getValue() too, so the two
     * sides are treated the same way and the sign is applied once, for net worth.
     *
     * Every row is converted to the preferred currency before it is added, and the
     * sums stay unrounded until the very end — each total is rounded once, to the
     * preferred currency's minor unit (PRD §4.5.3).
     */
    public PortfolioSummaryResponse buildSummary(User user) {
        String target = user.getPreferredCurrency();
        List<Asset> assets = assetRepository.findByUserOrdered(user);
        List<Liability> liabilities = liabilityRepository.findByUserOrdered(user);

        BigDecimal totalAssets = BigDecimal.ZERO;
        Map<Integer, BigDecimal> totalByCategoryId = new HashMap<>();
        for (Asset asset : assets) {
            BigDecimal value = currencyConverter.convert(asset.getValue(), asset.getCurrency(), target);
            totalAssets = totalAssets.add(value);
            totalByCategoryId.merge(asset.getCategory().getId(), value, BigDecimal::add);
        }

        BigDecimal totalLiabilities = BigDecimal.ZERO;
        for (Liability liability : liabilities) {
            totalLiabilities = totalLiabilities.add(
                    currencyConverter.convert(liability.getValue(), liability.getCurrency(), target));
        }

        PortfolioSummaryResponse response = new PortfolioSummaryResponse();
        response.setCurrency(target);
        response.setTotalAssets(currencyConverter.round(totalAssets, target));
        response.setTotalLiabilities(currencyConverter.round(totalLiabilities, target));
        response.setNetWorth(currencyConverter.round(totalAssets.subtract(totalLiabilities), target));
        response.setAssetCategories(categoryTotals(totalByCategoryId, target));
        return response;
    }

    // walk every seeded category in card order, so an empty category still gets a zero card
    private List<AssetCategoryTotal> categoryTotals(Map<Integer, BigDecimal> totalByCategoryId, String target) {
        List<AssetCategoryTotal> totals = new ArrayList<>();
        for (AssetCategory category : assetCategoryRepository.findAllOrdered()) {
            BigDecimal total = totalByCategoryId.getOrDefault(category.getId(), BigDecimal.ZERO);
            totals.add(new AssetCategoryTotal(
                    category.getId(),
                    category.getName(),
                    category.getDescription(),
                    currencyConverter.round(total, target)));
        }
        return totals;
    }
}
