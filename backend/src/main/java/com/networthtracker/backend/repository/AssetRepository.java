package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.Asset;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Integer> {

    /**
     * Every asset the user owns, in card order.
     *
     * display_order is a column on asset_categories now, so the ordering happens in
     * SQL. While categories were a java enum it could not, and AssetService had to
     * re-sort the list by hand.
     */
    @Query("SELECT a FROM Asset a JOIN a.category c "
            + "WHERE a.user = :user "
            + "ORDER BY c.displayOrder ASC, a.name ASC")
    List<Asset> findByUserOrdered(@Param("user") User user);

    // by id AND user, so another account's row is simply not found
    Optional<Asset> findByIdAndUser(Integer id, User user);
}
