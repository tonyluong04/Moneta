package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.Liability;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LiabilityRepository extends JpaRepository<Liability, Integer> {

    /** Everything the user owes, mortgages first. Ordered in SQL — see AssetRepository. */
    @Query("SELECT l FROM Liability l JOIN l.category c "
            + "WHERE l.user = :user "
            + "ORDER BY c.displayOrder ASC, l.name ASC")
    List<Liability> findByUserOrdered(@Param("user") User user);

    Optional<Liability> findByIdAndUser(Integer id, User user);
}
