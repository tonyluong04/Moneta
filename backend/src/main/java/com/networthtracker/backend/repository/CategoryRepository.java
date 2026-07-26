package com.networthtracker.backend.repository;

import com.networthtracker.backend.entity.Category;
import com.networthtracker.backend.entity.CategoryType;
import com.networthtracker.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Every category this user can see: the shared presets (user is null)
     * plus their own custom categories.
     */
    @Query("SELECT c FROM Category c "
            + "WHERE c.user IS NULL OR c.user = :user "
            + "ORDER BY c.type ASC, c.name ASC")
    List<Category> findAllVisibleTo(@Param("user") User user);

    /**
    * for referencing/using.
    * You use it in the budget service, when a user picks a category to budget against. 
    * The rule is looser: you may use any category that's visible to you — your own or a preset.
     */
    @Query("SELECT c FROM Category c "
            + "WHERE c.id = :id AND (c.user IS NULL OR c.user = :user)")
    Optional<Category> findVisibleById(@Param("id") Integer id, @Param("user") User user);

    /**
    * for editing/deleting. You use it in the category delete endpoint.
    * The rule there is stricter: you may only delete a category you own. 
    * Presets must be undeletable, and other people's categories untouchable.
     */
    Optional<Category> findByIdAndUser(Integer id, User user);

    boolean existsByUserAndNameIgnoreCaseAndType(User user, String name, CategoryType type);
}
