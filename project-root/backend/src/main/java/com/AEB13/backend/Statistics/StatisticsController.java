package com.AEB13.backend.Statistics;

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
}
