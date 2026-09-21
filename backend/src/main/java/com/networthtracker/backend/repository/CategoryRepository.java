package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Every category, in display order. Categories are shared presets seeded by
     * migration — there are no user-owned ones, so there is nothing to scope by user.
     */
    @Query("SELECT c FROM Category c ORDER BY c.type ASC, c.displayOrder ASC")
    List<Category> findAllOrdered();
}
