package com.AEB13.backend.WeeklyPlan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AEB13.backend.Meal.Meal;
import com.AEB13.backend.Meal.MealRepository;

@Service
public class WeeklyPlanService {

    @Autowired
    private WeeklyPlanRepository weeklyPlanRepository;


     @Autowired
    private MealRepository mealRepository;

    /**
     * Generates a shopping list for a given weekly plan.
     * 
     * @param weeklyPlanId The ID of the weekly plan.
     * @return A map with ingredients as keys and their total quantities as values.
     */
    public Map<String, String> generateShoppingList(Long weeklyPlanId) {
        WeeklyPlan weeklyPlan = weeklyPlanRepository.findById(weeklyPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly Plan not found"));

        Map<String, String> shoppingList = new HashMap<>();

        for (String day : weeklyPlan.getMeals().keySet()) {
            Long mealId = weeklyPlan.getMeals().get(day);
            Meal meal = mealRepository.findById(mealId)
                    .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

            // Parse ingredients and aggregate quantities
            parseAndAggregateIngredients(meal.getIngredients(), shoppingList);
        }

        return shoppingList;
    }

    /**
     * Parses the ingredients string and aggregates them into the shopping list.
     * 
     * @param ingredients The ingredients string (e.g., "Tomato - 2 cups, Onion - 3").
     * @param shoppingList The current shopping list map.
     */
    private void parseAndAggregateIngredients(String ingredients, Map<String, String> shoppingList) {
        String[] items = ingredients.split(","); // Split by comma to get individual items

        for (String item : items) {
            String[] parts = item.split(" - "); // Split by " - " to separate ingredient name and quantity
            if (parts.length == 2) {
                String ingredient = parts[0].trim(); // Ingredient name
                String quantity = parts[1].trim(); // Quantity

                // Aggregate quantities (simply concatenate for now; advanced logic can be added later)
                shoppingList.merge(ingredient, quantity, (oldValue, newValue) -> oldValue + " + " + newValue);
            }
        }
    }



    

    public WeeklyPlan getWeeklyPlan(Long id) {
        return weeklyPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan with ID " + id + " not found"));
    }

    public WeeklyPlan createWeeklyPlan(WeeklyPlan weeklyPlan) {
        Optional<WeeklyPlan> existingPlan = weeklyPlanRepository.findByWeek(weeklyPlan.getWeek());
        if (existingPlan.isPresent()) {
            throw new IllegalArgumentException("A plan for this week already exists.");
        }
        return weeklyPlanRepository.save(weeklyPlan);
    }


    public WeeklyPlan updateWeeklyPlan(Long id, Map<String, Long> meals) {
        WeeklyPlan plan = getWeeklyPlan(id);
        plan.setMeals(meals);
        return weeklyPlanRepository.save(plan);
    }

    public void deleteWeeklyPlan(Long id) {
        if (!weeklyPlanRepository.existsById(id)) {
            throw new RuntimeException("Plan with ID " + id + " does not exist");
        }
        weeklyPlanRepository.deleteById(id);
    }

    public List<WeeklyPlan> getAllWeeklyPlans() {
        return weeklyPlanRepository.findAll();
    }
}