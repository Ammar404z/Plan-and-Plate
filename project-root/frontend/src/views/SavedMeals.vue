<template>
  <div class="saved-meals">
    <h1>Your Saved Meals</h1>
    <ul v-if="meals.length > 0" class="meal-list">
      <li v-for="meal in meals" :key="meal.id" class="meal-card">
        <img :src="meal.thumbnail" alt="Meal Thumbnail" class="meal-image" />
        <div class="meal-info">
          <h3 class="meal-title">{{ meal.name }}</h3>
          <p><strong>Ingredients:</strong> {{ meal.ingredients }}</p>
          <p><strong>Instructions:</strong> {{ meal.instructions }}</p>
          <button @click="deleteMeal(meal.id)">Delete</button>
        </div>
      </li>
    </ul>
    <p v-else class="no-results">No meals saved yet.</p>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import { onMounted, ref } from "vue";

// Define the structure of a saved meal
interface Meal {
  id: string;
  name: string;
  ingredients: string;
  instructions: string;
  thumbnail: string;
}

const meals = ref<Meal[]>([]);

// Fetch saved meals on component mount
async function fetchMeals() {
  try {
    const response = await api.get("/api/meals");
    meals.value = response.data;
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

onMounted(fetchMeals);
</script>

<style scoped>
/* Styling similar to Recipe Search */
.saved-meals {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

h1 {
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

.meal-list {
  list-style: none;
  padding: 0;
}

.meal-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
  display: flex;
  align-items: center;
  transition: transform 0.3s, box-shadow 0.3s;
}

.meal-card:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.meal-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-right: 1px solid #ccc;
}

.meal-info {
  padding: 15px;
  flex: 1;
  text-align: left;
}

.meal-title {
  font-size: 1.2rem;
  color: #007bff;
  margin: 0 0 10px;
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
