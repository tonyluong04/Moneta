package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.AssetCategory;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Integer> {

    /** The presets plus this user's own, in display order. */
    @Query("SELECT c FROM AssetCategory c "
            + "WHERE c.user IS NULL OR c.user = :user "
            + "ORDER BY c.displayOrder ASC, c.name ASC")
    List<AssetCategory> findVisibleTo(@Param("user") User user);

    /**
     * One category, but only if this user is allowed to file an asset under it —
     * a preset, or one of their own. Someone else's custom category is not found.
     */
    @Query("SELECT c FROM AssetCategory c "
            + "WHERE c.id = :id AND (c.user IS NULL OR c.user = :user)")
    Optional<AssetCategory> findVisibleById(@Param("id") Integer id, @Param("user") User user);

    // own categories only — presets are never editable or deletable
    Optional<AssetCategory> findByIdAndUser(Integer id, User user);

    boolean existsByUserAndNameIgnoreCase(User user, String name);

    /** Where a newly created category sorts: after everything already visible. */
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM AssetCategory c "
            + "WHERE c.user IS NULL OR c.user = :user")
    int maxDisplayOrderFor(@Param("user") User user);
}
