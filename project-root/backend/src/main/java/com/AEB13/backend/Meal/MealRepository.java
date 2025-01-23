package com.AEB13.backend.Meal;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Meal} entities.
 * <p>
 * Provides CRUD operations and custom query methods for meal data.
 * </p>
 */
@Repository
public interface MealRepository extends CrudRepository<Meal, Long> {

    /**
     * Retrieves all Meal records from the database.
     *
     * @return a list of all Meal entities
     */
    List<Meal> findAll();

    /**
     * Deletes a Meal by its unique ID.
     *
     * @param id the ID of the Meal to be deleted
     */
    public void deleteById(Long id);

    /**
     * Finds the top 5 meals by their saved count in descending order.
     *
     * @return a list of up to 5 meals sorted by saved count descending
     */
    @Query("SELECT m FROM Meal m ORDER BY m.savedCount DESC")
    List<Meal> findTop5ByOrderBySavedCountDesc();

    /**
     * Finds a meal by its name.
     *
     * @param name the name of the meal
     * @return the Meal object if found, otherwise null
     */
    Meal findByName(String name);

    /**
     * Returns the sum of saved counts for all meals.
     *
     * @return the total saved count across all meals
     */
    @Query("SELECT SUM(m.savedCount) FROM Meal m")
    long sumAllSavedCounts();

    /**
     * Counts the meals by category where savedCount is greater than 0.
     *
     * @return a list of objects, where each object contains the category and the
     *         count
     */
    @Query("SELECT m.category, COUNT(m) FROM Meal m WHERE m.savedCount > 0 GROUP BY m.category")
    List<Object[]> countMealsByCategory();

    /**
     * 
     * @param name the name of the meal
     * @return List of Melas that fit the search criteria
     */
    List<Meal> findByNameContainingIgnoreCase(String name);
}
