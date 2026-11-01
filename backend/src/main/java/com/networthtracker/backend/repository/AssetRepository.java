package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.Asset;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// database access for assets.
public interface AssetRepository extends JpaRepository<Asset, Integer> {

    // everything this user owns, in category order then name (powers the portfolio page)
    List<Asset> findByUserOrderByCategoryAscNameAsc(User user);

    // find a row only if it is this user's (used before update/delete for safety)
    Optional<Asset> findByIdAndUser(Integer id, User user);
}
