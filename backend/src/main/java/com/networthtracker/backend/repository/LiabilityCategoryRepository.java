package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.LiabilityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LiabilityCategoryRepository extends JpaRepository<LiabilityCategory, Integer> {

    /** Every category, mortgages first. Seeded set — see AssetCategoryRepository. */
    @Query("SELECT c FROM LiabilityCategory c ORDER BY c.displayOrder ASC, c.name ASC")
    List<LiabilityCategory> findAllOrdered();
}
