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
 * Represents a weekly meal plan entity that associates specific meals with specific days.
 * <p>Each plan is identified by a unique week number and contains a mapping of days to meal IDs.</p>
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
     * <p>Uniqueness is enforced in the database to ensure one plan per week.</p>
     */
    @Column(unique = true)
    private int week;

    /**
     * A map associating days of the week (e.g., "Monday") with meal IDs.
     * <p>Stored as a collection table named "weekly_plan_meals".</p>
     */
    @ElementCollection
    @CollectionTable(name = "weekly_plan_meals", joinColumns = @JoinColumn(name = "weekly_plan_id"))
    @MapKeyColumn(name = "day")
    @Column(name = "meal_id")
    private Map<String, Long> meals = new HashMap<>();

    /**
     * Retrieves the unique identifier of the weekly plan.
     *
     * @return the plan ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the weekly plan.
     *
     * @param id the plan ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the week number for this plan.
     *
     * @return the week number
     */
    public int getWeek() {
        return week;
    }

    /**
     * Sets the week number for this plan.
     *
     * @param week the week number to set
     */
    public void setWeek(int week) {
        this.week = week;
    }

    /**
     * Retrieves the map of days to meal IDs for this plan.
     *
     * @return the mapping of days to meal IDs
     */
    public Map<String, Long> getMeals() {
        return meals;
    }

    /**
     * Sets the map of days to meal IDs for this plan.
     *
     * @param meals the new mapping of days to meal IDs
     */
    public void setMeals(Map<String, Long> meals) {
        this.meals = meals;
    }
}
