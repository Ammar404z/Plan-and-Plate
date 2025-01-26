<template>
  <div class="shopping-list-view">
    <h1>Shopping List</h1>

    <!-- Display a warning for skipped meals -->
    <div v-if="skippedMeals.length > 0" class="skipped-meals-warning">
      <p>Some meals were not found and have been skipped:</p>
      <ul>
        <li v-for="day in skippedMeals" :key="day">{{ day }}</li>
      </ul>
    </div>

    <!-- Display the shopping list -->
    <div v-if="shoppingList.length > 0">
      <ul class="ingredients-list">
        <li
          v-for="ingredient in shoppingList"
          :key="ingredient.name"
          class="ingredient-item"
        >
          <!-- Checkbox for marking ingredients as bought -->
          <input
            type="checkbox"
            v-model="ingredient.isChecked"
            class="ingredient-checkbox"
          />
          <!-- Display the ingredient thumbnail with a fallback for errors -->
          <img
            :src="getIngredientThumbnail(ingredient.name)"
            :alt="ingredient.name"
            class="ingredient-thumbnail"
            @error="setDefaultThumbnail($event)"
          />
          <!-- Display the ingredient name and quantity -->
          <span :class="{ checked: ingredient.isChecked }">
            {{ ingredient.name }}: {{ ingredient.quantity }}
          </span>
        </li>
      </ul>
    </div>
    <p v-else>Loading shopping list or no meals available...</p>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

// Define interfaces for ingredient and meal data
interface Ingredient {
  name: string;
  quantity: string;
  scaledQuantity: string;
  isChecked: boolean;
}

interface Meal {
  day: string;
  mealName: string;
  scalingFactor: number;
  ingredients: Ingredient[];
}

// Reactive variables for storing data
const shoppingList = ref<Meal[]>([]);
const skippedMeals = ref<string[]>([]);
const route = useRoute();

/**
 * Fetch the shopping list data from the backend and extracts the skipped meals, if any exist
 * maps the meals to the meal format of the frontend
 */
async function fetchShoppingList() {
  try {
    const planId = route.params.planId;
    if (!planId) {
      console.error("Plan ID is undefined. Cannot fetch shopping list.");
      return;
    }

    // Request the shopping list for the given plan ID
    const response = await api.get(`/api/shopping-list/${planId}`);
    console.log("Backend Response:", response.data);

    skippedMeals.value = response.data.skippedMeals || [];

    shoppingList.value = Object.entries(response.data.ingredients).map(
      ([name, quantity]) => ({
        name,
        quantity,
        isChecked: false, // Default state for the checkbox
      })
    );
  } catch (error) {
    console.error("Failed to fetch shopping list:", error); // Log errors
  }
}

function getIngredientThumbnail(ingredientName: string): string {
  const formattedName = ingredientName.split(" (")[0].trim(); // Remove extra details
  return `https://www.themealdb.com/images/ingredients/${formattedName}.png`;
}

function setDefaultThumbnail(event: Event): void {
  const target = event.target as HTMLImageElement;
  target.src = require("@/assets/grocery.png"); // Fallback image
}

/**
 * Fetch the shopping list when the component is mounted
 */
onMounted(() => {
  fetchShoppingList();
});
</script>

<style scoped>
.shopping-list-view {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.ingredients-list {
  list-style: none;
  padding: 0;
}

.ingredient-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.shopping-list-view h1 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.ingredient-thumbnail {
  width: 60px;
  height: 60px;
  margin-right: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.ingredients-list {
  list-style: none;
  padding: 0;
}

.ingredient-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.ingredient-thumbnail {
  width: 80px;
  height: 80px;
  margin-right: 10px;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.ingredient-checkbox {
  margin-right: 10px;
}

.checked {
  text-decoration: line-through;
  color: #999;
}

/* Warn about skipped meals */
.skipped-meals-warning {
  margin: 20px 0;
  padding: 15px;
  background-color: #fff3cd; /* Light yellow background */
  border: 1px solid #ffeeba; /* Light yellow border */
  border-radius: 8px;
  color: #856404;
}

.checked {
  text-decoration: line-through;
  color: #999;
}
</style>
