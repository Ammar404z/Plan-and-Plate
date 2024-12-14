package com.AEB13.backend.Meal;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository 
public interface MealRepository extends CrudRepository<Meal, Long>  {
    List<Meal> findAll();
    
    public void deleteById(Long id);
}
