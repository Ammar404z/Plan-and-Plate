package com.AEB13.backend.WeeklyPlan;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "weekly_plans")
public class WeeklyPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "weekly_plan_meals", joinColumns = @JoinColumn(name = "weekly_plan_id"))
    @MapKeyColumn(name = "day")
    @Column(name = "meal_id")
    private Map<String, Long> meals = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Long> getMeals() {
        return meals;
    }

    public void setMeals(Map<String, Long> meals) {
        this.meals = meals;
    }




}
