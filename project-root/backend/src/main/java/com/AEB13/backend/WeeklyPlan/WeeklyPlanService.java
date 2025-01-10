package com.AEB13.backend.WeeklyPlan;

import java.util.ArrayList;
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
     * Generates a shopping list grouped by meals, allowing individual scaling for
     * each meal.
     *
     * @param weeklyPlanId      The ID of the weekly plan.
     * @param defaultMultiplier The default scaling factor for meals.
     * @return A list of maps, each containing a meal's ingredients, day, and
     *         scaling factor.
     */
    public List<Map<String, Object>> generateShoppingList(Long weeklyPlanId, int defaultMultiplier) {
        WeeklyPlan weeklyPlan = weeklyPlanRepository.findById(weeklyPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly Plan not found"));

        List<Map<String, Object>> shoppingList = new ArrayList<>();

        for (String day : weeklyPlan.getMeals().keySet()) {
            Long mealId = weeklyPlan.getMeals().get(day);
            Meal meal = mealRepository.findById(mealId)
                    .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

            // Parse and scale ingredients for the current meal
            Map<String, Double> ingredientsMap = new HashMap<>();
            parseAndAggregateIngredients(meal.getIngredients(), ingredientsMap, defaultMultiplier);

            // Add meal details to the shopping list
            Map<String, Object> mealDetails = new HashMap<>();
            mealDetails.put("day", day);
            mealDetails.put("mealName", meal.getName());
            mealDetails.put("ingredients", formatShoppingList(ingredientsMap, defaultMultiplier)); // Format ingredients
            mealDetails.put("scalingFactor", defaultMultiplier); // Add scaling factor for frontend control
            shoppingList.add(mealDetails);
        }

        return shoppingList;
    }

    private void parseAndAggregateIngredients(String ingredients, Map<String, Double> consolidatedIngredients,
            int multiplier) {
        String[] items = ingredients.split(","); // Split by comma to get individual items

        for (String item : items) {
            String[] parts = item.split(" - "); // Split by " - " to separate ingredient name and quantity
            if (parts.length == 2) {
                String ingredient = parts[0].trim(); // Ingredient name
                String quantityString = parts[1].trim(); // Quantity (e.g., "2 cups" or "pinch")

                try {
                    // Handle numeric quantities with units
                    String[] quantityParts = quantityString.split(" ", 2);

                    double quantity = parseFraction(quantityParts[0]) * multiplier; // Handle fractions like "1/2"
                    String unit = quantityParts.length > 1 ? quantityParts[1].trim() : ""; // Extract unit if present

                    // Add to the consolidated map
                    String key = ingredient + (unit.isEmpty() ? "" : " (" + unit + ")");
                    consolidatedIngredients.merge(key, quantity, (oldVal, newVal) -> Math.max(0, oldVal + newVal));

                } catch (NumberFormatException e) {
                    // Handle non-numeric quantities (e.g., "pinch", "splash")
                    String key = ingredient + " (" + quantityString + ")";
                    consolidatedIngredients.putIfAbsent(key, -1.0); // Ensure non-scalable keys are added only once
                }
            }
        }
    }

    /**
     * Parses a fraction or a numeric value and converts it to a double.
     * 
     * @param input The input string (e.g., "1/2", "0.5").
     * @return The parsed numeric value.
     * @throws NumberFormatException if the input is not a valid number or fraction.
     */
    private double parseFraction(String input) throws NumberFormatException {
        if (input.contains("/")) {
            String[] fractionParts = input.split("/");
            if (fractionParts.length == 2) {
                double numerator = Double.parseDouble(fractionParts[0].trim());
                double denominator = Double.parseDouble(fractionParts[1].trim());
                return numerator / denominator;
            } else {
                throw new NumberFormatException("Invalid fraction format");
            }
        }
        return Double.parseDouble(input); // For standard numeric values
    }

    /**
     * Formats the consolidated ingredients map into a user-friendly shopping list.
     * 
     * @param consolidatedIngredients The map with scaled ingredient quantities.
     * @param multilplier             the number of portions/people
     * @return A formatted map with ingredients as keys and their quantities as
     *         values.
     */
    private Map<String, String> formatShoppingList(Map<String, Double> consolidatedIngredients, int multiplier) {
        Map<String, String> shoppingList = new HashMap<>();

        for (Map.Entry<String, Double> entry : consolidatedIngredients.entrySet()) {
            String ingredient = entry.getKey();
            Double quantity = entry.getValue();

            if (quantity == -1.0) {
                // For non-numerical ingredients, retain the original measurement
                shoppingList.put(ingredient, multiplier + "x original measurement");
            } else {
                // For numerical ingredients, format as usual
                shoppingList.put(ingredient, String.format("%.2f", quantity));
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