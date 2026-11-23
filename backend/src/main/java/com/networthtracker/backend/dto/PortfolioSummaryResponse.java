package com.networthtracker.backend.dto;

import java.math.BigDecimal;
import java.util.List;

// everything the portfolio overview draws, in one request: the hero rows and one
// total per asset category card. amounts only — the rings compute their own percentage.
public class PortfolioSummaryResponse {

    // the user's preferred currency: every row is converted into it before summing
    private String currency;

    private BigDecimal totalAssets;

    // positive, like Liability.balance — the hero renders the minus sign
    private BigDecimal totalLiabilities;

    // totalAssets − totalLiabilities, so this one can be negative
    private BigDecimal netWorth;

    // all 8 seeded categories in card order, zeros included
    private List<AssetCategoryTotal> assetCategories;

    public PortfolioSummaryResponse() {}

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public BigDecimal getTotalLiabilities() {
        return totalLiabilities;
    }

    public void setTotalLiabilities(BigDecimal totalLiabilities) {
        this.totalLiabilities = totalLiabilities;
    }

    public BigDecimal getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(BigDecimal netWorth) {
        this.netWorth = netWorth;
    }

    public List<AssetCategoryTotal> getAssetCategories() {
        return assetCategories;
    }

    public void setAssetCategories(List<AssetCategoryTotal> assetCategories) {
        this.assetCategories = assetCategories;
    }
}
