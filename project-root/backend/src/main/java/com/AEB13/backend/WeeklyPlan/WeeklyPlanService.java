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

/**
 * Service class containing business logic for managing and generating data
 * related to {@link WeeklyPlan}.
 */
@Service
public class WeeklyPlanService {

    /**
     * Repository to perform database operations on {@link WeeklyPlan} entities.
     */
    @Autowired
    private WeeklyPlanRepository weeklyPlanRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeeklyPlanService.class);

    /**
     * Repository to perform database operations on {@link Meal} entities.
     */
    @Autowired
    private MealRepository mealRepository;

    /**
     * Generates a shopping list grouped by meals within a specified weekly plan.
     * <p>
     * The default multiplier scales ingredient quantities for each meal.
     * </p>
     *
     * @param weeklyPlanId      the ID of the weekly plan
     * @param defaultMultiplier the default scaling factor for each meal
     * @return a map representing the shopping list
     * @throws IllegalStateException if no valid meals are found in the plan
     */
    public Map<String, Object> generateShoppingList(Long weeklyPlanId) {
        WeeklyPlan weeklyPlan = getWeeklyPlan(weeklyPlanId);

        Map<String, Integer> nonNumericCounts = new HashMap<>(); // New map for non-numeric counts
        Map<String, Double> aggregatedIngredients = new HashMap<>();
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

                // Get scaling factor for the day or default to 1
                Integer scalingFactor = weeklyPlan.getPortionSizes().getOrDefault(day, 1);

                // Parse and scale ingredients for the current meal
                parseAndAggregateIngredients(meal.getIngredients(), aggregatedIngredients, scalingFactor,
                        nonNumericCounts);

            } catch (Exception e) {
                logger.warn("Failed to process meal for {}: {}", day, e.getMessage());
                skippedMeals.add(day);
            }
        }

        if (aggregatedIngredients.isEmpty()) {
            throw new IllegalStateException("No valid meals found in the weekly plan");
        }

        // Format the aggregated ingredients for output
        Map<String, String> formattedIngredients = formatShoppingList(aggregatedIngredients, nonNumericCounts);

        // Create the final shopping list result
        Map<String, Object> shoppingListResult = new HashMap<>();
        shoppingListResult.put("ingredients", formattedIngredients);
        shoppingListResult.put("skippedMeals", skippedMeals);

        return shoppingListResult;
    }

    /**
     * Parses the ingredient string from a Meal and accumulates scaled quantities in
     * a consolidated map.
     *
     * @param ingredients             the raw ingredient string from the Meal
     * @param consolidatedIngredients the map to store scaled ingredient data
     * @param multiplier              the scaling factor for each ingredient
     * @param nonNumericCounts        the number of counts for no numeric quantities
     */
    private void parseAndAggregateIngredients(String ingredients, Map<String, Double> consolidatedIngredients,
            int multiplier, Map<String, Integer> nonNumericCounts) {
        String[] items = ingredients.split(",");

        for (String item : items) {
            String[] parts = item.split(" - ");
            if (parts.length == 2) {
                String ingredient = parts[0].trim();
                String quantityString = parts[1].trim();

                try {
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

                        // Combine unit with any extra information
                        String fullUnit = unit + (extraInfo.isEmpty() ? "" : " " + extraInfo);

                        // Key for the consolidated map: "ingredient (unit info)"
                        String key = ingredient + (fullUnit.isEmpty() ? "" : " (" + fullUnit + ")");
                        consolidatedIngredients.merge(key, quantity, (oldVal, newVal) -> Math.max(0, oldVal + newVal));
                    } else {
                        // Handle non-numeric quantities
                        String key = ingredient + " (" + quantityString + ")";
                        consolidatedIngredients.putIfAbsent(key, -1.0);
                        nonNumericCounts.merge(key, multiplier, Integer::sum); // Add the multiplier to the count
                    }
                } catch (NumberFormatException e) {
                    // Handle non-numeric quantities
                    String key = ingredient + " (" + quantityString + ")";
                    consolidatedIngredients.putIfAbsent(key, -1.0);
                }
            }
        }
    }

    /**
     * Converts common fraction characters (¼, ½, ¾) to their numeric equivalents
     * ("1/4", "1/2", "3/4").
     *
     * @param input the original string potentially containing special fraction
     *              characters
     * @return the string with special fractions replaced by numeric fractions
     */
    private String convertSpecialFractions(String input) {
        return input
                .replace("¼", "1/4")
                .replace("½", "1/2")
                .replace("¾", "3/4");
    }

    /**
     * Normalizes various unit strings to a standard format (e.g., "tablespoon" ->
     * "tbsp").
     *
     * @param unit the original unit string
     * @return a normalized unit string
     */
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
     * Parses a fraction or numeric string into a double.
     * <p>
     * Handles both fraction (e.g., "1/2") and decimal (e.g., "0.5") formats.
     * </p>
     *
     * @param input the string to parse
     * @return the numeric value
     * @throws NumberFormatException if the format is invalid
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
        return Double.parseDouble(input);
    }

    /**
     * Converts a map of scaled ingredients to a formatted map for display or
     * output.
     *
     * @param consolidatedIngredients the map containing ingredient keys and numeric
     *                                values
     * @param multiplier              the scaling factor used for the plan
     * @param nonNumericCounts        the number of counts for no numeric quantities
     * @return a map of ingredient keys and their formatted quantities
     */
    private Map<String, String> formatShoppingList(Map<String, Double> consolidatedIngredients,
            Map<String, Integer> nonNumericCounts) {
        Map<String, String> shoppingList = new HashMap<>();

        for (Map.Entry<String, Double> entry : consolidatedIngredients.entrySet()) {
            String ingredient = entry.getKey();
            Double quantity = entry.getValue();

            if (quantity != -1.0) {
                shoppingList.put(ingredient, String.format("%.2f", quantity));
            }
        }

        for (Map.Entry<String, Integer> entry : nonNumericCounts.entrySet()) {
            String ingredient = entry.getKey();
            Integer totalMultiplier = entry.getValue(); // Total count includes scaling
            shoppingList.put(ingredient, totalMultiplier + "x " + ingredient.split("\\(")[1].replace(")", ""));
        }

        return shoppingList;
    }

    /**
     * Sorts a map of day-to-meal associations in a natural weekly order (Monday ->
     * Sunday).
     *
     * @param meals the unsorted map of days to meal IDs
     * @return a new map sorted by day of the week
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

    /**
     * Retrieves a {@link WeeklyPlan} by its ID, sorting its meals by day.
     *
     * @param id the ID of the plan
     * @return the retrieved plan
     * @throws RuntimeException if the plan does not exist
     */
    public WeeklyPlan getWeeklyPlan(Long id) {
        WeeklyPlan weeklyPlan = weeklyPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan with ID " + id + " not found"));

        // Sort the meals within the weekly plan by day
        Map<String, Long> sortedMeals = sortDays(weeklyPlan.getMeals());
        weeklyPlan.setMeals(sortedMeals);

        return weeklyPlan;
    }

    /**
     * Creates a new weekly plan for a specified week, if it does not already exist.
     *
     * @param weeklyPlan the new weekly plan to create
     * @return the saved {@link WeeklyPlan} entity
     * @throws IllegalArgumentException if a plan for the specified week already
     *                                  exists
     */
    public WeeklyPlan createWeeklyPlan(WeeklyPlan weeklyPlan) {
        Optional<WeeklyPlan> existingPlan = weeklyPlanRepository.findByWeek(weeklyPlan.getWeek());
        if (existingPlan.isPresent()) {
            throw new IllegalArgumentException("A plan for this week already exists.");
        }
        // Validate portion sizes
        if (weeklyPlan.getPortionSizes() != null) {
            for (String day : weeklyPlan.getPortionSizes().keySet()) {
                if (!weeklyPlan.getMeals().containsKey(day)) {
                    throw new IllegalArgumentException("Portion size specified for a day without a meal.");
                }
            }
        }
        return weeklyPlanRepository.save(weeklyPlan);
    }

    /**
     * Updates an existing weekly plan's day-to-meal mappings and saves it.
     *
     * @param id    the ID of the weekly plan to update
     * @param meals the new mapping of days to meal IDs
     * @return the updated {@link WeeklyPlan} entity
     */
    public WeeklyPlan updateWeeklyPlan(Long id, WeeklyPlan weeklyPlan) {
        WeeklyPlan plan = getWeeklyPlan(id);
        plan.setMeals(weeklyPlan.getMeals());
        plan.setPortionSizes(weeklyPlan.getPortionSizes());
        return weeklyPlanRepository.save(plan);
    }

    /**
     * Deletes a weekly plan by its ID.
     *
     * @param id the ID of the weekly plan to delete
     * @throws RuntimeException if the plan does not exist
     */
    public void deleteWeeklyPlan(Long id) {
        if (!weeklyPlanRepository.existsById(id)) {
            throw new RuntimeException("Plan with ID " + id + " does not exist");
        }
        weeklyPlanRepository.deleteById(id);
    }

    /**
     * Retrieves all weekly plans, sorts each plan's meals by day, and optionally
     * sorts the plans by week.
     *
     * @return a list of all sorted {@link WeeklyPlan} entities
     */
    public List<WeeklyPlan> getAllWeeklyPlans() {
        List<WeeklyPlan> weeklyPlans = weeklyPlanRepository.findAll();

        // Sort the meals in each weekly plan by day
        weeklyPlans.forEach(plan -> {
            Map<String, Long> sortedMeals = sortDays(plan.getMeals());
            plan.setMeals(sortedMeals);
        });

        // Sort the list of weekly plans by their week number
        weeklyPlans.sort(Comparator.comparing(WeeklyPlan::getWeek));

        return weeklyPlans;
    }
}
