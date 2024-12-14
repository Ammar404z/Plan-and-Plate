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
     * Generates a shopping list for a given weekly plan, scaled for the specified number of people.
     * 
     * @param weeklyPlanId The ID of the weekly plan.
     * @param multiplier   The scaling factor (e.g., 2 for 2 people, 3 for 3 people).
     * @return A map with ingredients as keys and their total scaled quantities as values.
     */
    public Map<String, String> generateShoppingList(Long weeklyPlanId, int multiplier) {
        WeeklyPlan weeklyPlan = weeklyPlanRepository.findById(weeklyPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly Plan not found"));

        Map<String, Double> consolidatedIngredients = new HashMap<>();

        for (String day : weeklyPlan.getMeals().keySet()) {
            Long mealId = weeklyPlan.getMeals().get(day);
            Meal meal = mealRepository.findById(mealId)
                    .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

            // Parse and scale ingredients
            parseAndAggregateIngredients(meal.getIngredients(), consolidatedIngredients, multiplier);
        }

        // Format the shopping list for display
        return formatShoppingList(consolidatedIngredients);
    }

    /**
     * Parses the ingredients string, scales quantities, and aggregates them into the consolidated map.
     * 
     * @param ingredients            The ingredients string (e.g., "Tomato - 2 cups, Onion - 3").
     * @param consolidatedIngredients The map to store consolidated and scaled ingredients.
     * @param multiplier             The scaling factor for quantities.
     */
    private void parseAndAggregateIngredients(String ingredients, Map<String, Double> consolidatedIngredients, int multiplier) {
        String[] items = ingredients.split(","); // Split by comma to get individual items

        for (String item : items) {
            String[] parts = item.split(" - "); // Split by " - " to separate ingredient name and quantity
            if (parts.length == 2) {
                String ingredient = parts[0].trim(); // Ingredient name
                String quantityString = parts[1].trim(); // Quantity (e.g., "2 cups")

                try {
                    // Handle numeric quantities with units
                    String[] quantityParts = quantityString.split(" ", 2);

                    double quantity = Double.parseDouble(quantityParts[0]) * multiplier; // Scale the quantity
                    String unit = quantityParts.length > 1 ? quantityParts[1].trim() : ""; // Extract unit if present

                    // Add to the consolidated map
                    String key = ingredient + (unit.isEmpty() ? "" : " (" + unit + ")");
                    consolidatedIngredients.merge(key, quantity, Double::sum);
                } catch (NumberFormatException e) {
                    // Handle non-numeric quantities (e.g., "As required")
                    consolidatedIngredients.put(ingredient, -1.0); // Use -1.0 to indicate non-scalable
                }
            }
        }
    }

    /**
     * Formats the consolidated ingredients map into a user-friendly shopping list.
     * 
     * @param consolidatedIngredients The map with scaled ingredient quantities.
     * @return A formatted map with ingredients as keys and their quantities as values.
     */
    private Map<String, String> formatShoppingList(Map<String, Double> consolidatedIngredients) {
        Map<String, String> shoppingList = new HashMap<>();

        for (Map.Entry<String, Double> entry : consolidatedIngredients.entrySet()) {
            if (entry.getValue() == -1.0) {
                shoppingList.put(entry.getKey(), "As required");
            } else {
                shoppingList.put(entry.getKey(), String.format("%.2f", entry.getValue()));
            }
        }

        return shoppingList;
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