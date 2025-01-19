package com.AEB13.backend.Statistics;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AEB13.backend.Meal.MealRepository;

/**
 * REST controller that provides endpoints for retrieving statistical data
 * about meals.
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    /**
     * Repository for performing database operations on meals.
     */
    @Autowired
    private MealRepository mealRepository;

    /**
     * Retrieves the total saved count of all meals.
     *
     * @return a {@link ResponseEntity} containing the total number of times
     *         all meals have been saved
     */
    @GetMapping("/total-saved-count")
    public ResponseEntity<Long> getTotalSavedCount() {
        long totalSavedCount = mealRepository.sumAllSavedCounts();
        return ResponseEntity.ok(totalSavedCount);
    }

    /**
     * Retrieves the distribution of meals by category, specifically those that have a saved count above zero.
     *
     * @return a {@link ResponseEntity} containing a map of category names to the
     *         count of meals in each category
     */
    @GetMapping("/category-distribution")
    public ResponseEntity<Map<String, Long>> getCategoryDistribution() {
        List<Object[]> categoryCounts = mealRepository.countMealsByCategory();
        Map<String, Long> distribution = categoryCounts.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],  // Category name
                        obj -> (Long) obj[1]     // Count of meals in that category
                ));
        return ResponseEntity.ok(distribution);
    }
}
