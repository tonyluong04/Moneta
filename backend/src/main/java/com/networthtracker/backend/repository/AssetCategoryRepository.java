package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Integer> {

    /**
     * Every category, in card order.
     *
     * No user scoping: the set is seeded by V10 and the same for everyone, the way
     * CategoryRepository.findAllOrdered() serves the budget categories. Lookup by id
     * comes free from JpaRepository.
     */
    @Query("SELECT c FROM AssetCategory c ORDER BY c.displayOrder ASC, c.name ASC")
    List<AssetCategory> findAllOrdered();
}
