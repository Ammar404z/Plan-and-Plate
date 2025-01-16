package com.AEB13.backend.WeeklyPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AEB13.backend.Meal.Meal;
import com.AEB13.backend.Meal.MealRepository;

@Service
public class WeeklyPlanService {

    @Autowired
    private WeeklyPlanRepository weeklyPlanRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeeklyPlanService.class);

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
        WeeklyPlan weeklyPlan = getWeeklyPlan(weeklyPlanId);

        List<Map<String, Object>> shoppingList = new ArrayList<>();
        List<String> skippedMeals = new ArrayList<>();

        for (Map.Entry<String, Long> entry : weeklyPlan.getMeals().entrySet()) {
            String day = entry.getKey();
            Long mealId = entry.getValue();

            try {
                Optional<Meal> mealOptional = mealRepository.findById(mealId);

                if (mealOptional.isEmpty()) {
                    skippedMeals.add(day);
                    continue;
                }

                Meal meal = mealOptional.get();

                // Parse and scale ingredients for the current meal
                Map<String, Double> ingredientsMap = new HashMap<>();
                parseAndAggregateIngredients(meal.getIngredients(), ingredientsMap, defaultMultiplier);

                // Add meal details to the shopping list
                Map<String, Object> mealDetails = new HashMap<>();
                mealDetails.put("day", day);
                mealDetails.put("mealName", meal.getName());
                mealDetails.put("ingredients", formatShoppingList(ingredientsMap, defaultMultiplier));
                mealDetails.put("scalingFactor", defaultMultiplier);

                // Add skipped meals information if any
                if (!skippedMeals.isEmpty()) {
                    mealDetails.put("skippedMeals", skippedMeals);
                }

                shoppingList.add(mealDetails);

            } catch (Exception e) {
                // Log the error and continue with next meal
                logger.warn("Failed to process meal for {}: {}", day, e.getMessage());
                skippedMeals.add(day);
            }
        }

        if (shoppingList.isEmpty()) {
            throw new IllegalStateException("No valid meals found in the weekly plan");
        }

        return shoppingList;
    }

    private void parseAndAggregateIngredients(String ingredients, Map<String, Double> consolidatedIngredients,
            int multiplier) {
        String[] items = ingredients.split(",");

        for (String item : items) {
            String[] parts = item.split(" - ");
            if (parts.length == 2) {
                String ingredient = parts[0].trim();
                String quantityString = parts[1].trim();

                try {
                    // Enhanced regex pattern to handle fractions (including ¼, ½, ¾) and various
                    // unit formats
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                            "^([\\d./¼½¾]+)\\s*([a-zA-Z]+|tsp|tbsp|kg|g|ml|oz|cup|teaspoon|tablespoon)?\\s*(\\(.*\\))?$",
                            java.util.regex.Pattern.CASE_INSENSITIVE);
                    java.util.regex.Matcher matcher = pattern.matcher(quantityString);

                    if (matcher.find()) {
                        String numericPart = matcher.group(1);
                        String unit = matcher.group(2) != null ? normalizeUnit(matcher.group(2)) : "";
                        String extraInfo = matcher.group(3) != null ? matcher.group(3).trim() : "";

                        // Convert special fraction characters to numeric values
                        numericPart = convertSpecialFractions(numericPart);

                        double quantity = parseFraction(numericPart) * multiplier;

                        // Combine unit with any extra information (like "Medium" for eggs)
                        String fullUnit = unit + (extraInfo.isEmpty() ? "" : " " + extraInfo);

                        // Add to the consolidated map
                        String key = ingredient + (fullUnit.isEmpty() ? "" : " (" + fullUnit + ")");
                        consolidatedIngredients.merge(key, quantity, (oldVal, newVal) -> Math.max(0, oldVal + newVal));
                    } else {
                        // Handle non-numeric quantities (e.g., "Dusting")
                        String key = ingredient + " (" + quantityString + ")";
                        consolidatedIngredients.putIfAbsent(key, -1.0);
                    }
                } catch (NumberFormatException e) {
                    // Handle non-numeric quantities
                    String key = ingredient + " (" + quantityString + ")";
                    consolidatedIngredients.putIfAbsent(key, -1.0);
                }
            }
        }
    }

    private String convertSpecialFractions(String input) {
        return input
                .replace("¼", "1/4")
                .replace("½", "1/2")
                .replace("¾", "3/4");
    }

    private String normalizeUnit(String unit) {
        if (unit == null)
            return "";

        unit = unit.toLowerCase().trim();
        switch (unit) {
            case "tsp":
            case "teaspoon":
                return "tsp";
            case "tbsp":
            case "tablespoon":
                return "tbsp";
            case "g":
            case "gram":
            case "grams":
                return "g";
            case "kg":
            case "kilogram":
            case "kilograms":
                return "kg";
            case "ml":
            case "milliliter":
            case "milliliters":
                return "ml";
            case "oz":
            case "ounce":
            case "ounces":
                return "oz";
            default:
                return unit;
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

    /**
     * Sorts the meal plan by day of the week.
     *
     * @param meals The map with days as keys and meal IDs as values.
     * @return A sorted map with days as keys and meal IDs as values.
     */
    private Map<String, Long> sortDays(Map<String, Long> meals) {
        List<String> dayOrder = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
                "Sunday");
        return meals.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> dayOrder.indexOf(entry.getKey())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    public WeeklyPlan getWeeklyPlan(Long id) {
        WeeklyPlan weeklyPlan = weeklyPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan with ID " + id + " not found"));

        // Sort the meals within the weekly plan by day
        Map<String, Long> sortedMeals = sortDays(weeklyPlan.getMeals());
        weeklyPlan.setMeals(sortedMeals);

        return weeklyPlan;
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
        List<WeeklyPlan> weeklyPlans = weeklyPlanRepository.findAll();

        // Sort the meals in each weekly plan by day
        weeklyPlans.forEach(plan -> {
            Map<String, Long> sortedMeals = sortDays(plan.getMeals());
            plan.setMeals(sortedMeals);
        });

        // Optionally, sort the weekly plans themselves (e.g., by week number)
        weeklyPlans.sort(Comparator.comparing(WeeklyPlan::getWeek));

        return weeklyPlans;

    }
}