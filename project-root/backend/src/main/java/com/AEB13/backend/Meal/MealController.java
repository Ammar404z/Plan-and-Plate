package com.AEB13.backend.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller class that handles HTTP requests for Meal-related endpoints.
 */
@RestController
public class MealController {

    /**
     * Base URL for TheMealDB API, injected from application properties.
     */
    @Value("${themealdb.api.url}")
    private String themealdbApiUrl;

    /**
     * A RestTemplate instance for making external API calls.
     */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Repository for CRUD operations on Meal entities.
     */
    @Autowired
    private MealRepository mealRepository;

    /**
     * Service layer for handling Meal business logic.
     */
    @Autowired
    private MealService mealService;

    /**
     * Adds a meal to the database. If the meal already exists, returns an error
     * response. Otherwise, creates a new record.
     *
     * @param meal the Meal object to be added
     * @return the saved Meal entity or an error response if it already exists
     */
    @PostMapping("/api/meals/add")
    public ResponseEntity<?> addMeal(@RequestBody Meal meal) {
        try {
            Meal savedMeal = mealService.addMeal(meal); // Service layer now handles duplicates
            return ResponseEntity.ok(savedMeal);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    /**
     * Adds a custom meal to the database.
     * <p>
     * This endpoint accepts a Meal object with custom details and saves it
     * if it does not already exist.
     * </p>
     *
     * @param meal the custom Meal object to be added
     * @return the saved custom Meal entity
     */
    @PostMapping("/api/meals/add-custom")
    public ResponseEntity<Meal> addCustomMeal(@RequestBody Meal meal) {
        Meal savedMeal = mealService.addCustomMeal(meal);
        return ResponseEntity.ok(savedMeal);
    }

    /**
     * Filters meals based on a specific category or area.
     * <p>
     * Only one filter can be applied at a time.
     * </p>
     *
     * @param category the category to filter by (optional)
     * @param area     the area to filter by (optional)
     * @return the filtered list of meals or an error message
     */
    @GetMapping("api/meals/filter")
    public ResponseEntity<?> filterMeals(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String area) {

        if ((category == null || category.isEmpty()) && (area == null || area.isEmpty())) {
            return ResponseEntity.badRequest().body("At least one filter (category or area) must be specified.");
        }

        String filterEndpoint;

        if (category != null && !category.isEmpty()) {
            filterEndpoint = themealdbApiUrl + "/filter.php?c=" + category;
        } else {
            filterEndpoint = themealdbApiUrl + "/filter.php?a=" + area;
        }

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(filterEndpoint, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching filtered meals: " + e.getMessage());
        }
    }

    /**
     * Retrieves a combined list of categories and areas from TheMealDB API.
     *
     * @return a list containing both meal categories and areas
     */
    @GetMapping("/api/meals/categoriesAndAreas")
    public ResponseEntity<?> getCategoriesAndAreas() {
        String categoriesUrl = themealdbApiUrl + "/list.php?c=list";
        String areasUrl = themealdbApiUrl + "/list.php?a=list";

        try {
            ResponseEntity<String> categoriesResponse = restTemplate.getForEntity(categoriesUrl, String.class);
            ResponseEntity<String> areasResponse = restTemplate.getForEntity(areasUrl, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> categories = (List<Map<String, String>>) objectMapper
                    .readValue(categoriesResponse.getBody(), Map.class).get("meals");
            List<Map<String, String>> areas = (List<Map<String, String>>) objectMapper
                    .readValue(areasResponse.getBody(), Map.class).get("meals");

            List<Map<String, String>> combinedFilters = new ArrayList<>();
            for (Map<String, String> category : categories) {
                combinedFilters.add(Map.of("type", "Category", "value", category.get("strCategory")));
            }
            for (Map<String, String> area : areas) {
                combinedFilters.add(Map.of("type", "Area", "value", area.get("strArea")));
            }

            Map<String, Object> response = Map.of("filters", combinedFilters);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching categories and areas: " + e.getMessage());
        }
    }

    /**
     * Retrieves all meals from the local database.
     *
     * @return a list of all Meal entities
     */
    @GetMapping("/api/meals")
    public List<Meal> getAllMeals() {
        return mealService.getAllMeals();
    }

    /**
     * Deletes a specific meal by its ID.
     *
     * @param id the ID of the meal to be deleted
     * @return a no-content response on success
     */
    @DeleteMapping("/api/meals/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Toggles the favorite status of a specific meal by its ID.
     *
     * @param id the ID of the meal whose favorite status to toggle
     * @return the updated Meal object, or 404 if not found
     */
    @PutMapping("/api/meals/{id}")
    public ResponseEntity<Meal> toggleFavorite(@PathVariable Long id) {
        Optional<Meal> mealOptional = mealRepository.findById(id);
        if (mealOptional.isPresent()) {
            Meal meal = mealOptional.get();
            meal.setFavorite(!meal.isFavorite());
            mealRepository.save(meal);
            return ResponseEntity.ok(meal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Searches for custom meals by name.
     *
     * @param name the name (or partial name) of the custom meal to search
     * @return a list of custom meals that match the search criteria
     */
    @GetMapping("/api/meals/custom/search")
    public List<Meal> searchCustomMeals(@RequestParam String name) {
        return mealRepository.findByNameContainingIgnoreCase(name);
    }

    // all search logic in one method(filter, search, custom meal search and search
    // + filter logic)
    @GetMapping("/api/meals/search-filter")
    public ResponseEntity<List<Meal>> searchAndFilterMeals(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String area) {

        if ((category == null || category.isEmpty()) && (area == null || area.isEmpty())) {
            if (name != null && !name.isEmpty()) {
                List<Meal> meals = mealService.searchMealByName(name);
                return ResponseEntity.ok(meals);
            }
        }

        List<Meal> combinedMeals = new ArrayList<>();

        // Fetch meals from MealDB based on category or area
        if (category != null || area != null) {
            String filterEndpoint = (category != null)
                    ? themealdbApiUrl + "/filter.php?c=" + category
                    : themealdbApiUrl + "/filter.php?a=" + area;

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(filterEndpoint, String.class);
                List<Meal> filteredMeals = mealService.parseMealDBResponse(response.getBody());
                combinedMeals.addAll(filteredMeals);
            } catch (Exception e) {
                return ResponseEntity.status(500).body(null);
            }
        }

        // Fetch custom meals from the database
        if (name != null) {
            combinedMeals.addAll(mealRepository.findByNameContainingIgnoreCase(name));
        } else {
            combinedMeals.addAll(mealRepository.findAll());
        }

        // Apply search logic
        if (name != null) {
            combinedMeals = combinedMeals.stream()
                    .filter(meal -> meal.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Remove duplicates
        Map<Long, Meal> uniqueMeals = combinedMeals.stream()
                .collect(Collectors.toMap(Meal::getId, meal -> meal, (existing, replacement) -> existing));

        return ResponseEntity.ok(new ArrayList<>(uniqueMeals.values()));
    }

}
