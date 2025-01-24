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

    /**
     * Retrieves the category of this meal.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of this meal.
     *
     * @param category the new category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Retrieves the cooked count of this meal.
     *
     * @return the number of times this meal has been cooked
     */
    public int getCookedCount() {
        return cookedCount;
    }

    /**
     * Sets the number of times this meal has been cooked.
     *
     * @param cookedCount the new cooked count
     */
    public void setCookedCount(int cookedCount) {
        this.cookedCount = cookedCount;
    }

    /**
     * Retrieves the saved count of this meal.
     *
     * @return the number of times this meal has been saved
     */
    public int getSavedCount() {
        return savedCount;
    }

    /**
     * Sets the number of times this meal has been saved.
     *
     * @param savedCount the new saved count
     */
    public void setSavedCount(int savedCount) {
        this.savedCount = savedCount;
    }

    /**
     * Retrieves the thumbnail URL of this meal.
     *
     * @return the thumbnail URL
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets the thumbnail URL of this meal.
     *
     * @param thumbnail the new thumbnail URL
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * Retrieves the unique identifier of this meal.
     *
     * @return the meal ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this meal.
     *
     * @param id the new meal ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the name of this meal.
     *
     * @return the meal name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this meal.
     *
     * @param name the new meal name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the ingredients of this meal.
     *
     * @return the meal ingredients
     */
    public String getIngredients() {
        return ingredients;
    }

    /**
     * Sets the ingredients of this meal.
     *
     * @param ingredients the new ingredients
     */
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Retrieves the cooking instructions for this meal.
     *
     * @return the cooking instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Sets the cooking instructions for this meal.
     *
     * @param instructions the new cooking instructions
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Retrieves the YouTube video link associated with this meal.
     *
     * @return the YouTube video URL
     */
    public String getYouTubeVid() {
        return youTubeVid;
    }

    /**
     * Sets the YouTube video link for this meal.
     *
     * @param youTubeVid the new YouTube video URL
     */
    public void setYouTubeVid(String youTubeVid) {
        this.youTubeVid = youTubeVid;
    }

    /**
     * Retrieves the external API ID of this meal.
     *
     * @return the API ID
     */
    public String getApiId() {
        return apiId;
    }

    /**
     * Sets the external API ID for this meal.
     *
     * @param apiId the new API ID
     */
    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    /**
     * Checks if this meal is marked as favorite.
     *
     * @return true if favorite, false otherwise
     */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     * Marks or unmarks this meal as a favorite.
     *
     * @param favorite the new favorite status
     */
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
