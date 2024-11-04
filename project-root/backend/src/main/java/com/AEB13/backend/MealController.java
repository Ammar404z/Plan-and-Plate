package com.AEB13.backend;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}