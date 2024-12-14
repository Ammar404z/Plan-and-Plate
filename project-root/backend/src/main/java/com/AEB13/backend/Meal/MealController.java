package com.AEB13.backend.Meal;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MealController {
    
    @Autowired
    private MealService mealService;

    @GetMapping("/api/meals/search")
    public Map<String, Object> searchMealByName(@RequestParam String name) {
        return mealService.searchMealByName(name);
    }

    
    @PostMapping("/api/meals/add")
    public ResponseEntity<Meal> addMeal(@RequestBody Meal meal) {
        System.out.println("Received Meal: " + meal.getName()); // Debug log
        Meal savedMeal = mealService.addMeal(meal);
        System.out.println("Saved Meal: " + savedMeal.getName()); // Debug log
        return ResponseEntity.ok(savedMeal);
    }

    @GetMapping("/api/meals")
    public List<Meal> getAllMeals() {
        return mealService.getAllMeals();
    }

    @DeleteMapping("/api/meals/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}