package com.AEB13.backend.WeeklyPlan;

import java.util.Collections;
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

@RestController
@RequestMapping("/api")
public class WeeklyPlanController {

    @Autowired
    private WeeklyPlanService weeklyPlanService;


     /**
     * Endpoint to generate a shopping list for a weekly plan.
     * 
     * @param weeklyPlanId The ID of the weekly plan.
     * @return A consolidated shopping list.
     */
    @GetMapping("/shopping-list/{weeklyPlanId}")
    public ResponseEntity<Map<String, String>> generateShoppingList(@PathVariable Long weeklyPlanId) {
        try {
            Map<String, String> shoppingList = weeklyPlanService.generateShoppingList(weeklyPlanId);
            return ResponseEntity.ok(shoppingList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    @GetMapping("/create-weekly-plans/{id}")
    public ResponseEntity<WeeklyPlan> getWeeklyplan(@PathVariable Long id) {
        return ResponseEntity.ok(weeklyPlanService.getWeeklyPlan(id));
    }
    @GetMapping("/create-weekly-plans")
    public ResponseEntity<List<WeeklyPlan>> getAllWeeklyPlans() {
        List<WeeklyPlan> weeklyPlans = weeklyPlanService.getAllWeeklyPlans();
        return ResponseEntity.ok(weeklyPlans);
    }
    @PostMapping("/create-weekly-plans")
    public ResponseEntity<?> createWeeklyPlan(@RequestBody WeeklyPlan weeklyPlan) {
        try {
            WeeklyPlan savedPlan = weeklyPlanService.createWeeklyPlan(weeklyPlan);
            return ResponseEntity.ok(savedPlan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/create-weekly-plans/{id}")
    public ResponseEntity<WeeklyPlan> updateWeeklyPlan(@PathVariable Long id, @RequestBody Map<String, Long> meals) {
        return ResponseEntity.ok(weeklyPlanService.updateWeeklyPlan(id, meals));
    }

    @DeleteMapping("/create-weekly-plans/{id}")
    public ResponseEntity<Void> deleteWeeklyPlan(@PathVariable Long id) {
        weeklyPlanService.deleteWeeklyPlan(id);
        return ResponseEntity.noContent().build();
    }
}