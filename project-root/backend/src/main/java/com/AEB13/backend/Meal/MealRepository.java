package com.AEB13.backend.Meal;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository 
public interface MealRepository extends CrudRepository<Meal, Long>  {

    @Query("SELECT m FROM Meal m WHERE m.deleted = false") // do this to execlude the soft deleted meals
    List<Meal> findAll();
    
    public void deleteById(Long id);

    @Query("SELECT m FROM Meal m ORDER BY m.savedCount DESC")
    List<Meal> findTop5ByOrderBySavedCountDesc(); 

    Meal findByName(String name);
}
