package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.Liability;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// database access for liabilities.
public interface LiabilityRepository extends JpaRepository<Liability, Integer> {

    // everything this user owes, in category order then name
    List<Liability> findByUserOrderByCategoryAscNameAsc(User user);

    // find a row only if it is this user's (used before update/delete for safety)
    Optional<Liability> findByIdAndUser(Integer id, User user);
}
