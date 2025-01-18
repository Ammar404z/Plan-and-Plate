package com.AEB13.backend.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

@RestController
public class MealController {
    @Value("${themealdb.api.url}")
    private String themealdbApiUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private MealRepository mealRepository;

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

    @PostMapping("/api/meals/add-custom") // add custom meal endpoint here. This endpoint should accept a custom meal
                                          // object and save it to the database.
    public ResponseEntity<Meal> addCustomMeal(@RequestBody Meal meal) {
        Meal savedMeal = mealService.addCustomMeal(meal);
        return ResponseEntity.ok(savedMeal);
    }

    /*
     * TODO: add the logic in the service layer
     */
    @GetMapping("api/meals/filter")
    public ResponseEntity<?> filterMeals(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String area) {

        // Ensure only one filter is applied at a time
        if ((category == null || category.isEmpty()) && (area == null || area.isEmpty())) {
            return ResponseEntity.badRequest().body("At least one filter (category or area) must be specified.");
        }

        String filterEndpoint;

        if (category != null && !category.isEmpty()) {
            filterEndpoint = themealdbApiUrl + "/filter.php?c=" + category;
        } else {
            filterEndpoint = themealdbApiUrl + "/filter.php?a=" + area;
        }

        // Fetch filtered meals from TheMealDB API
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(filterEndpoint, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching filtered meals: " + e.getMessage());
        }
    }

    /*
     * TODO: add the logic in the service layer
     */
    @GetMapping("/api/meals/categoriesAndAreas")
    public ResponseEntity<?> getCategoriesAndAreas() {
        String categoriesUrl = themealdbApiUrl + "/list.php?c=list"; // Categories endpoint
        String areasUrl = themealdbApiUrl + "/list.php?a=list"; // Areas endpoint

        try {
            // Fetch categories and areas from TheMealDB API
            ResponseEntity<String> categoriesResponse = restTemplate.getForEntity(categoriesUrl, String.class);
            ResponseEntity<String> areasResponse = restTemplate.getForEntity(areasUrl, String.class);

            // Parse responses into JSON format
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> categories = (List<Map<String, String>>) objectMapper
                    .readValue(categoriesResponse.getBody(), Map.class).get("meals");
            List<Map<String, String>> areas = (List<Map<String, String>>) objectMapper
                    .readValue(areasResponse.getBody(), Map.class).get("meals");

            // Combine categories and areas into a single list with type and value
            List<Map<String, String>> combinedFilters = new ArrayList<>();
            for (Map<String, String> category : categories) {
                combinedFilters.add(Map.of("type", "Category", "value", category.get("strCategory")));
            }
            for (Map<String, String> area : areas) {
                combinedFilters.add(Map.of("type", "Area", "value", area.get("strArea")));
            }

            // Return the combined list as a single key "filters"
            Map<String, Object> response = Map.of("filters", combinedFilters);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching categories and areas: " + e.getMessage());
        }
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

    @GetMapping("/api/statistics/top-saved-recipes")
    public List<Meal> getTopSavedMeals() {
        List<Meal> meals = mealRepository.findTop5ByOrderBySavedCountDesc();
        System.out.println("Top saved meals: " + meals); // Debug log
        return meals;
    }

    @PutMapping("/api/meals/{id}")
    public ResponseEntity<Meal> toggleFavorite(@PathVariable Long id) {
        Optional<Meal> mealOptional = mealRepository.findById(id);
        if (mealOptional.isPresent()) {
            Meal meal = mealOptional.get();
            meal.setFavorite(!meal.isFavorite()); // Toggle the favorite status
            mealRepository.save(meal); // Save the updated meal
            return ResponseEntity.ok(meal); // Return the updated meal
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if meal doesn't exist
        }
    }
}