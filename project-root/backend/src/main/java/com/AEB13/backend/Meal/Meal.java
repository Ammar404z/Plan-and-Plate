package com.AEB13.backend.Meal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a Meal entity in the application.
 *
 * <p>
 * This entity stores various attributes of a meal, including its name,
 * ingredients, cooking instructions, associated media, and usage statistics.
 * </p>
 */
@Entity
@Table(name = "meals")
public class Meal {

    /**
     * The unique identifier for this Meal.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the meal.
     * <p>
     * Cannot be null.
     * </p>
     */
    @Column(nullable = false)
    private String name;

    /**
     * The ingredients used in the meal, stored as text.
     */
    @Column(columnDefinition = "TEXT")
    private String ingredients;

    /**
     * The detailed cooking instructions for the meal, stored as long text.
     */
    @Column(columnDefinition = "LONGTEXT")
    private String instructions;

    /**
     * The URL or path to the thumbnail image for this meal.
     */
    private String thumbnail;

    /**
     * The category to which this meal belongs (e.g., "Dessert", "Vegan").
     */
    private String category;

    /**
     * A YouTube video URL demonstrating how to cook this meal.
     */
    private String youTubeVid;

    /**
     * An optional external API ID for referencing this meal from another source.
     */
    private String apiId;

    /**
     * The number of times this meal has been saved by users.
     */
    @Column
    private int savedCount = 0;

    /**
     * The number of times this meal has been cooked by users.
     */
    @Column
    private int cookedCount = 0;

    /**
     * Indicates whether this meal is marked as a favorite.
     */
    @Column(nullable = false)
    private boolean favorite = false;

    @Column(nullable = false)
    private boolean isCustom = false;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCookedCount() {
        return cookedCount;
    }

    public void setCookedCount(int cookedCount) {
        this.cookedCount = cookedCount;
    }

    public int getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(int savedCount) {
        this.savedCount = savedCount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getYouTubeVid() {
        return youTubeVid;
    }

    public void setYouTubeVid(String youTubeVid) {
        this.youTubeVid = youTubeVid;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

}
