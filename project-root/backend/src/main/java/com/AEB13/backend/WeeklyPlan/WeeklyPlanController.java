package com.AEB13.backend.WeeklyPlan;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that manages operations related to weekly plans.
 */
@RestController
@RequestMapping("/api")
public class WeeklyPlanController {

    /**
     * Service layer to handle business logic for weekly plans.
     */
    @Autowired
    private WeeklyPlanService weeklyPlanService;

    /**
     * Generates and retrieves a shopping list for a specific weekly plan.
     * <p>The shopping list is grouped by days and scaled based on a default multiplier.</p>
     *
     * @param planId the ID of the weekly plan
     * @return a list of maps containing ingredient details for each day
     */
    @GetMapping("/shopping-list/{planId}")
    public ResponseEntity<List<Map<String, Object>>> getShoppingList(@PathVariable Long planId) {
        return ResponseEntity.ok(weeklyPlanService.generateShoppingList(planId, 1));
    }

    /**
     * Retrieves a specific weekly plan by its ID.
     *
     * @param id the ID of the weekly plan
     * @return the requested {@link WeeklyPlan} object
     */
    @GetMapping("/create-weekly-plans/{id}")
    public ResponseEntity<WeeklyPlan> getWeeklyplan(@PathVariable Long id) {
        return ResponseEntity.ok(weeklyPlanService.getWeeklyPlan(id));
    }

    /**
     * Retrieves all weekly plans in the system.
     *
     * @return a list of all existing {@link WeeklyPlan} objects
     */
    @GetMapping("/create-weekly-plans")
    public ResponseEntity<List<WeeklyPlan>> getAllWeeklyPlans() {
        List<WeeklyPlan> weeklyPlans = weeklyPlanService.getAllWeeklyPlans();
        return ResponseEntity.ok(weeklyPlans);
    }

    /**
     * Creates a new weekly plan.
     * <p>A plan for the specified week will be rejected if it already exists.</p>
     *
     * @param weeklyPlan the new weekly plan to create
     * @return the created {@link WeeklyPlan} if successful, otherwise an error message
     */
    @PostMapping("/create-weekly-plans")
    public ResponseEntity<?> createWeeklyPlan(@RequestBody WeeklyPlan weeklyPlan) {
        try {
            WeeklyPlan savedPlan = weeklyPlanService.createWeeklyPlan(weeklyPlan);
            return ResponseEntity.ok(savedPlan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates an existing weekly plan by replacing its day-to-meal associations.
     *
     * @param id    the ID of the plan to update
     * @param meals a map of days to meal IDs
     * @return the updated {@link WeeklyPlan} object
     */
    @PutMapping("/create-weekly-plans/{id}")
    public ResponseEntity<WeeklyPlan> updateWeeklyPlan(@PathVariable Long id, @RequestBody Map<String, Long> meals) {
        return ResponseEntity.ok(weeklyPlanService.updateWeeklyPlan(id, meals));
    }

    /**
     * Deletes a weekly plan by its ID.
     *
     * @param id the ID of the weekly plan to delete
     * @return a no-content response on successful deletion
     */
    @DeleteMapping("/create-weekly-plans/{id}")
    public ResponseEntity<Void> deleteWeeklyPlan(@PathVariable Long id) {
        weeklyPlanService.deleteWeeklyPlan(id);
        return ResponseEntity.noContent().build();
    }
}
