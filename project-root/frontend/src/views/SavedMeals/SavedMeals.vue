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

    <!-- List of saved meals -->
    <ul v-if="filteredMeals.length > 0" class="meal-list">
      <li v-for="meal in filteredMeals" :key="meal.id" class="meal-card">
        <!-- Header row with image next to title -->
        <div class="meal-header">
          <img :src="meal.thumbnail" alt="Meal Thumbnail" class="meal-image" />
          <h3 class="meal-title">{{ meal.name }}</h3>
        </div>
        <button @click="deleteMeal(meal.id)">Delete Meal</button>
        <button
          v-if="meal.youTubeVid"
          @click="watchYouTubeVid(meal.youTubeVid)"
          class="youtube-button"
        >
          ► Watch Video
        </button>
        <button @click="viewMeal(meal.id)">View Meal</button>
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
  name: string;
  ingredients: string;
  instructions: string;
  thumbnail: string;
  category: string;
  youTubeVid: string;
}

const meals = ref<Meal[]>([]);
const searchQuery = ref(""); // Search query
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
async function viewMeal(mealId: number) {
  router.push(`/view-meal/${mealId}`);
}

// Computed property to filter meals based on the search query
const filteredMeals = computed(() => {
  if (!searchQuery.value.trim()) {
    return meals.value;
  }
  return meals.value.filter((meal) =>
    meal.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

onMounted(fetchMeals);
</script>

<style scoped>
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
.youtube-button {
  background-color: #ff0000;
  color: white;
  border: none;
  padding: 10px 15px;
  font-size: 0.9rem;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  position: absolute; /* Allow placement within the card */
  bottom: 10px; /* Align with the bottom */
  right: 10px; /* Align with the right */
}

.youtube-button:hover {
  background-color: #cc0000;
}
/* Styling similar to Recipe Search */
.saved-meals {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

/* Title Box Styling */
.title-box {
  background-color: #f0f8ff; /* Light blue background */
  border: 2px solid #007bff; /* Blue border */
  padding: 10px 20px;
  border-radius: 10px;
  display: inline-block;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* Subtle shadow */
  margin-bottom: 10px;
}

.title-box h1 {
  font-size: 1.8rem; /* Adjusted size for balance */
  margin: 0;
  color: #333;
  font-family: "Georgia", serif; /* Add a classy font */
  text-align: center;
}

/* Add Meal Button Styling */
.add-meal-container {
  margin-top: 10px; /* Add some space between title and button */
}

.add-meal-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.3s;
  width: 100%; /* Full-width button */
  max-width: 300px; /* Restrict maximum width */
}

.add-meal-btn:hover {
  background-color: #0056b3;
}

.meal-list {
  list-style: none;
  padding: 0;
}

.meal-card {
  position: relative; /* Enable positioning for the child elements */
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
  padding: 15px;
  text-align: left;
  min-height: 200px; /* Ensure sufficient height for the button placement */
}

/* Header row for image + title side-by-side */
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
  color: #e60026; /* lava-red for consistency with nav */
  margin: 0;
}

button {
  background-color: #f56c6c;
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #d9534f;
}

.no-results {
  font-size: 1rem;
  color: #888;
  margin-top: 20px;
}
</style>
