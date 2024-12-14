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

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> searchMealByName(String name) {
        String url = String.format("%s/search.php?s=%s", apiUrl, name);
        return restTemplate.getForObject(url, Map.class);
    }

    public Meal addMeal(Meal meal){
        return mealRepository.save(meal);
    }

    public List<Meal> getAllMeals(){
        return mealRepository.findAll();
    }

    public void deleteById(Long id) {
        mealRepository.deleteById(id);
     }
    
    }