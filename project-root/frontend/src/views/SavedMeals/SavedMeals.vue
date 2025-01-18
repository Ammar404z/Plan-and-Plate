<template>
  <div class="saved-meals">
    <!-- Title with a box and custom font -->
    <div class="title-box">
      <h1>Your Saved Meals</h1>
    </div>

    <!-- Add Custom Meal Button -->
    <div class="add-meal-container">
      <button @click="navigateToAddMeal" class="add-meal-btn">
        Add Custom Meal
      </button>
    </div>

    <!-- Search Bar -->
    <div class="search-container">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Search saved meals..."
        class="search-bar"
      />
    </div>

    <!-- Sort Toggle Button -->
    <div class="sort-container">
      <button @click="toggleSortOrder" class="sort-button">
        Sort: {{ sortOrder === "asc" ? "A-Z" : "Z-A" }}
      </button>
    </div>

    <!-- List of saved meals -->
    <ul v-if="sortedMeals.length > 0" class="meal-list">
      <li v-for="meal in sortedMeals" :key="meal.id" class="meal-card">
        <!-- Favorite button positioned in the top-right corner -->
        <button @click="toggleFavorite(meal)" class="favorite-button">
          <font-awesome-icon
            :icon="[meal.favorite ? 'fas' : 'far', 'star']"
            style="color: #ffd43b; font-size: 2rem"
          />
        </button>

        <!-- Thumbnail Image -->
        <img :src="meal.thumbnail" alt="Meal Thumbnail" class="meal-image" />

        <!-- Title below the thumbnail -->
        <h3 class="meal-title">{{ meal.name }}</h3>

        <!-- Details for custom meals -->
        <div v-if="!meal.apiId" class="meal-details">
          <p><strong>Ingredients:</strong> {{ meal.ingredients }}</p>
          <p><strong>Instructions:</strong> {{ meal.instructions }}</p>
        </div>

        <!-- Buttons -->
        <button @click="deleteMeal(meal.id)">Delete Meal</button>
        <button v-if="meal.apiId" @click="viewMeal(meal.apiId)">
          View Meal
        </button>
        <button
          v-if="meal.youTubeVid"
          @click="watchYouTubeVid(meal.youTubeVid)"
          class="youtube-button"
        >
          ► Watch Video
        </button>
      </li>
    </ul>

    <!-- No meals found -->
    <p v-else class="no-results">No meals found.</p>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

// Define the structure of a saved meal
interface Meal {
  id: string;
  apiId: string;
  name: string;
  ingredients: string;
  instructions: string;
  thumbnail: string;
  category: string;
  youTubeVid: string;
  favorite: boolean;
}

const meals = ref<Meal[]>([]);
const searchQuery = ref(""); // Search query
const sortOrder = ref("asc"); // Default sorting order
const router = useRouter();

function navigateToAddMeal() {
  router.push("/add-meal");
}

async function watchYouTubeVid(youTubeVid: string) {
  window.open(youTubeVid, "_blank");
}

// Fetch saved meals on component mount
async function fetchMeals() {
  try {
    const response = await api.get("/api/meals");
    meals.value = response.data;
    console.log(response);
  } catch (error) {
    console.error("Error fetching meals:", error);
  }
}

async function deleteMeal(id: string) {
  try {
    await api.delete(`/api/meals/${id}`);
    meals.value = meals.value.filter((meal) => meal.id !== id);
    alert("Meal deleted successfully!");
  } catch (error) {
    console.error("Error deleting meal:", error);
    alert("Failed to delete meal.");
  }
}

async function viewMeal(mealId: string) {
  router.push(`/view-meal/${mealId}`);
}

async function toggleFavorite(meal: Meal) {
  try {
    const response = await api.put(`/api/meals/${meal.id}`);
    const updatedMeal = response.data;
    meal.favorite = updatedMeal.favorite;
  } catch (error) {
    console.error("Error toggling favorite:", error);
  }
}

// Method to toggle sorting order
function toggleSortOrder() {
  sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
}

// Computed property to filter and sort meals
const sortedMeals = computed(() => {
  let filtered = meals.value;
  if (searchQuery.value.trim()) {
    filtered = filtered.filter((meal) =>
      meal.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }
  return filtered.sort((a, b) => {
    if (sortOrder.value === "asc") {
      return a.name.localeCompare(b.name);
    } else {
      return b.name.localeCompare(a.name);
    }
  });
});

onMounted(fetchMeals);
</script>

<style scoped>
/* Container of the entire Saved Meals component */
.saved-meals {
  max-width: 1200px; /* Increased max width for a wider layout */
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

/* Title Box Styling */
.title-box {
  font-size: 1.2rem;
  padding: 15px 20px;
  background-color: #e3f6e8; /* Lighter green background */
  color: #28a745; /* Green text */
  border: 2px solid #89cff0; /* Similar to dropdown */
  border-radius: 10px; /* Similar to dropdown */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Similar to dropdown */
  display: flex;
  justify-content: center;
  align-items: center;
  width: 250px; /* Match the Add Custom Meal width */
  margin: 0 auto 10px auto; /* Center and add spacing below */
}

.title-box h1 {
  font-size: 1.2rem;
  margin: 0;
  color: #28a745;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  text-align: center;
}

/* Add Meal Button container and style */
.add-meal-container {
  margin-top: 10px;
}

.add-meal-btn {
  background-color: #e3f6e8; /* Light green background */
  font-size: 1.2rem;
  color: #28a745; /* Green text */
  border: 2px solid #89cff0;
  padding: 15px 20px;
  cursor: pointer;
  border-radius: 10px;
  transition: background-color 0.3s;
  width: 290px; /* Match the title box width */
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin: 10px auto;
}

.add-meal-btn:hover {
  background-color: #218838;
  color: #fff;
  transform: translateY(-2px);
}

/* Search Container */
.search-container {
  margin: 20px 0;
  text-align: center;
}

.search-bar {
  width: 100%;
  max-width: 400px;
  padding: 10px;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  outline: none;
}

.search-bar:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

/* Sort Button */
.sort-container {
  text-align: right;
  margin-bottom: 20px;
}

.sort-button {
  padding: 5px 10px;
  font-size: 1rem;
  border: 1px solid #007bff;
  border-radius: 5px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}

.sort-button:hover {
  background-color: #0056b3;
}

/* Meal List */
.meal-list {
  list-style: none;
  padding: 0;
  display: flex; /* Use flex layout for vertical alignment */
  flex-wrap: wrap; /* Allow multiple rows */
  gap: 20px; /* Add spacing between meal cards */
  justify-content: center; /* Center-align meals */
}

/* Meal Card */
.meal-card {
  position: relative; /* Ensure the star button is positioned relative to this card */
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 15px;
  text-align: left;
  width: 300px; /* Set a fixed width for meal cards */
  min-height: 350px; /* Ensure consistent height for all cards */
}

/* Meal Header (Thumbnail + Title) */
.meal-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.meal-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  margin-right: 15px;
  border-radius: 8px;
}

.meal-title {
  font-size: 1.3rem;
  color: #e60026;
  margin: 0;
  font-weight: bold;
}

/* Buttons in the meal card */
.meal-card button {
  border: none;
  border-radius: 5px;
  margin-right: 10px;
  margin-top: 8px;
  padding: 8px 12px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

/* Delete Meal */
.meal-card button:nth-of-type(2) {
  background-color: #dc3545; /* Red for Delete Meal */
  color: #fff;
}
.meal-card button:nth-of-type(2):hover {
  background-color: #c82333;
}

/* View Meal */
.meal-card button:nth-of-type(3) {
  background-color: #007bff; /* Blue for View Meal */
  color: #fff;
}
.meal-card button:nth-of-type(3):hover {
  background-color: #0056b3;
}

/* YouTube Button */
.youtube-button {
  background-color: #ff0000;
  color: white;
  border: none;
  padding: 10px 15px;
  font-size: 0.9rem;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  position: absolute;
  bottom: 10px;
  right: 10px;
}

.youtube-button:hover {
  background-color: #cc0000;
}

/* Favorite button */
.favorite-button {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 1.5rem;
  color: gray;
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.3s ease;
}

/* No Results */
.no-results {
  font-size: 1rem;
  color: #888;
  margin-top: 20px;
}
</style>
