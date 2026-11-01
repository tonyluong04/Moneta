package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.LiabilityCategory;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LiabilityCategoryRepository extends JpaRepository<LiabilityCategory, Integer> {

    /** The presets plus this user's own, in display order. */
    @Query("SELECT c FROM LiabilityCategory c "
            + "WHERE c.user IS NULL OR c.user = :user "
            + "ORDER BY c.displayOrder ASC, c.name ASC")
    List<LiabilityCategory> findVisibleTo(@Param("user") User user);

    @Query("SELECT c FROM LiabilityCategory c "
            + "WHERE c.id = :id AND (c.user IS NULL OR c.user = :user)")
    Optional<LiabilityCategory> findVisibleById(@Param("id") Integer id,
                                                @Param("user") User user);

    Optional<LiabilityCategory> findByIdAndUser(Integer id, User user);

    boolean existsByUserAndNameIgnoreCase(User user, String name);

    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM LiabilityCategory c "
            + "WHERE c.user IS NULL OR c.user = :user")
    int maxDisplayOrderFor(@Param("user") User user);
}
