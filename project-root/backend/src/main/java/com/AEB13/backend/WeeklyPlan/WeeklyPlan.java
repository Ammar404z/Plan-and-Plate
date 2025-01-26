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

/**
 * Represents a weekly meal plan entity that associates specific meals with
 * specific days.
 * <p>
 * Each plan is identified by a unique week number and contains a mapping of
 * days to meal IDs.
 * </p>
 */
@Entity
@Table(name = "weekly_plans")
public class WeeklyPlan {

    /**
     * The unique identifier for this weekly plan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The week number for this plan.
     * <p>
     * Uniqueness is enforced in the database to ensure one plan per week.
     * </p>
     */
    @Column(unique = true)
    private int week;

    /**
     * A map associating days of the week (e.g., "Monday") with meal IDs.
     * <p>
     * Stored as a collection table named "weekly_plan_meals".
     * </p>
     */
    @ElementCollection
    @CollectionTable(name = "weekly_plan_meals", joinColumns = @JoinColumn(name = "weekly_plan_id"))
    @MapKeyColumn(name = "day")
    @Column(name = "meal_id")
    private Map<String, Long> meals = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "weekly_plan_portion_sizes", joinColumns = @JoinColumn(name = "weekly_plan_id"))
    @MapKeyColumn(name = "day")
    @Column(name = "portion_size")
    private Map<String, Integer> portionSizes = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public Map<String, Long> getMeals() {
        return meals;
    }

    public void setMeals(Map<String, Long> meals) {
        this.meals = meals;
    }

    public Map<String, Integer> getPortionSizes() {
        return portionSizes;
    }

    public void setPortionSizes(Map<String, Integer> portionSizes) {
        this.portionSizes = portionSizes;
    }
}
