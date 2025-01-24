package com.AEB13.backend.Meal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service class for handling business logic related to {@link Meal} operations.
 */
@Service
public class MealService {

    /**
     * Repository for executing CRUD operations on Meal entities.
     */
    @Autowired
    private MealRepository mealRepository;

    /**
     * Base URL for TheMealDB API, injected from application properties.
     */
    @Value("${themealdb.api.url}")
    private String apiUrl;

    /**
     * Default thumbnail image URL to use if none is provided.
     */
    private static final String DEAFULT_TUMBNAIL_URL = "https://png.pngtree.com/png-clipart/20191120/original/pngtree-meal-icon-vectors-png-image_5053746.jpg";

    /**
     * A RestTemplate instance for making external API calls.
     */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Searches for a meal by name using TheMealDB API.
     *
     * @param name the name of the meal to search
     * @return a map containing the JSON response from TheMealDB API
     */
    public List<Meal> searchMealByName(String name) {
        String url = String.format("%s/search.php?s=%s", apiUrl, name);
        try {
            String response = restTemplate.getForObject(url, String.class);
            return parseMealDBResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list if there is an error
        }
    }

    /**
     * Adds a meal to the database or increments the saved count if it already
     * exists.
     *
     * @param meal the Meal object to be added or updated
     * @return the persisted Meal object
     */
    public Meal addMeal(Meal meal) {
        // Check if a meal with the same name already exists
        Meal existingMeal = mealRepository.findByName(meal.getName());
        if (existingMeal != null) {
            throw new RuntimeException("Meal with name '" + meal.getName() + "' already exists.");
        }

        meal.setSavedCount(1); // Initialize saved count for a new meal
        return mealRepository.save(meal);
    }

    /**
     * Adds a custom meal to the database, setting a default thumbnail if none is
     * provided.
     *
     * @param meal the custom Meal object to be saved
     * @return the newly created Meal entity
     * @throws RuntimeException if a meal with the same name already exists
     */
    public Meal addCustomMeal(Meal meal) {
        Meal existingMeal = mealRepository.findByName(meal.getName());
        if (existingMeal != null) {
            throw new RuntimeException("A meal with the same name already exists.");
        }

        if (meal.getThumbnail() == null || meal.getThumbnail().trim().isEmpty()) {
            meal.setThumbnail(DEAFULT_TUMBNAIL_URL);
        }

        meal.setSavedCount(1);
        meal.setCustom(true);
        return mealRepository.save(meal);
    }

    /**
     * Retrieves all meals from the local database.
     *
     * @return a list of all Meal entities
     */
    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    /**
     * Deletes a meal by its ID.
     *
     * @param id the ID of the meal to be deleted
     * @throws RuntimeException if the meal is not found
     */
    public void deleteById(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        mealRepository.delete(meal);
    }

    public List<Meal> parseMealDBResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, String>> meals = (List<Map<String, String>>) responseMap.get("meals");

            List<Meal> mealList = new ArrayList<>();
            for (Map<String, String> mealData : meals) {
                Meal meal = new Meal();
                meal.setId(Long.parseLong(mealData.get("idMeal"))); // Assuming idMeal is a Long
                meal.setName(mealData.get("strMeal"));
                meal.setThumbnail(mealData.get("strMealThumb"));
                // Add more fields as required
                mealList.add(meal);
            }
            return mealList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
