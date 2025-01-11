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

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private MealRepository mealRepository;

    @GetMapping("/total-saved-count")
    public ResponseEntity<Long> getTotalSavedCount() {
        long totalSavedCount = mealRepository.sumAllSavedCounts();
        return ResponseEntity.ok(totalSavedCount);
    }

    @GetMapping("/category-distribution")
    public ResponseEntity<Map<String, Long>> getCategoryDistribution() {
        List<Object[]> categoryCounts = mealRepository.countMealsByCategory();
        Map<String, Long> distribution = categoryCounts.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0], // Category name
                        obj -> (Long) obj[1] // Count of meals
                ));
        return ResponseEntity.ok(distribution);
    }
}
