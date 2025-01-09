package com.AEB13.backend.Meal;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    @Value("${themealdb.api.url}")
    private String apiUrl;

    private static final String DEAFULT_TUMBNAIL_URL = "https://png.pngtree.com/png-clipart/20191120/original/pngtree-meal-icon-vectors-png-image_5053746.jpg";
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> searchMealByName(String name) {
        String url = String.format("%s/search.php?s=%s", apiUrl, name);
        return restTemplate.getForObject(url, Map.class);
    }

    public Meal addMeal(Meal meal) {
        Meal existingMeal = mealRepository.findByName(meal.getName());
        if (existingMeal != null) {
            existingMeal.setSavedCount(existingMeal.getSavedCount() + 1);
            return mealRepository.save(existingMeal);
        } else {
            meal.setSavedCount(1); // Initialize the count for a new meal
            return mealRepository.save(meal);
        }
    }

    /*
     * TODO:
     * - handle the statistcs implementation like above
     */
    public Meal addCustomMeal(Meal meal) {
        // Check if a meal with the same name already exists
        Meal existingMeal = mealRepository.findByName(meal.getName());
        if (existingMeal != null) {
            throw new RuntimeException("A meal with th same name already exists.");
        }

        // set the default thumbnail for the new custom meal
        if (meal.getThumbnail() == null || meal.getThumbnail().trim().isEmpty()) {
            meal.setThumbnail(DEAFULT_TUMBNAIL_URL);
        }

        // save the new custom meal
        return mealRepository.save(meal);
    }

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public void deleteById(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        mealRepository.delete(meal);
    }
}