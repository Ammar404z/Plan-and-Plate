<template>
  <div class="shopping-list-view">
    <h1>Shopping List</h1>

    <!-- Display a warning for any skipped meals -->
    <div v-if="skippedMeals.length > 0" class="skipped-meals-warning">
      <p>Some meals were not found and have been skipped:</p>
      <ul>
        <!-- List the days for which meals were skipped -->
        <li v-for="day in skippedMeals" :key="day">{{ day }}</li>
      </ul>
    </div>

    <!-- Display the shopping list if there are meals available -->
    <div v-if="shoppingList.length > 0">
      <!-- Loop through the shopping list and display each meal's details -->
      <div v-for="meal in shoppingList" :key="meal.mealName" class="meal-group">
        <!-- Display the day and meal name -->
        <h3>{{ meal.day }} - {{ meal.mealName }}</h3>

        <!-- Allow the user to adjust portion size -->
        <div class="meal-scaling">
          <label for="scaling">Portion Size:</label>
          <input
            type="number"
            v-model.number="meal.scalingFactor"
            @input="updateMealScaling(meal)"
            placeholder="Enter portion size"
            min="1"
          />
        </div>

        <!-- Display the list of ingredients for the meal -->
        <ul>
          <li
            v-for="ingredient in meal.ingredients"
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
            <!-- Display the ingredient name and scaled quantity -->
            <span :class="{ checked: ingredient.isChecked }">
              {{ ingredient.name }}: {{ ingredient.scaledQuantity }}
            </span>
          </li>
        </ul>
      </div>
    </div>
    <!-- Message for when the shopping list is loading or empty -->
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
  scalingFactor: number; // Portion size for the meal
  ingredients: Ingredient[]; // List of ingredients for the meal
}

// Reactive variables for storing data
const shoppingList = ref<Meal[]>([]); // Array of meals in the shopping list
const skippedMeals = ref<string[]>([]); // Days with skipped meals
const route = useRoute(); // Get route information

// Fetch the shopping list data from the backend
async function fetchShoppingList() {
  try {
    const planId = route.params.planId; // Get the plan ID from the route
    if (!planId) {
      console.error("Plan ID is undefined. Cannot fetch shopping list.");
      return;
    }

    // Request the shopping list for the given plan ID
    const response = await api.get(`/api/shopping-list/${planId}`);
    console.log("Backend Response:", response.data);

    // Extract skipped meals from the first entry in the response, if any
    if (response.data[0]?.skippedMeals) {
      skippedMeals.value = response.data[0].skippedMeals;
    }

    // Map the backend data to the shoppingList format
    shoppingList.value = response.data.map((meal: any) => ({
      ...meal,
      ingredients: Object.entries(meal.ingredients).map(([name, quantity]) => ({
        name,
        quantity,
        scaledQuantity: quantity, // Initially set to the original quantity
        isChecked: false, // Default state for the checkbox
      })),
    }));
  } catch (error) {
    console.error("Failed to fetch shopping list:", error); // Log errors
  }
}

// Update the quantities of ingredients based on the scaling factor
function updateMealScaling(meal: any) {
  meal.ingredients.forEach((ingredient: any) => {
    // Handle ingredients with "original measurement" text
    if (ingredient.quantity.includes("original measurement")) {
      ingredient.scaledQuantity = `${meal.scalingFactor}x original measurement`;
    } else {
      // Split the quantity into numeric and unit parts for scaling
      const [amount, unit] = ingredient.quantity.split(" ");
      if (!isNaN(parseFloat(amount))) {
        ingredient.scaledQuantity = `${(
          parseFloat(amount) * meal.scalingFactor
        ).toFixed(2)} ${unit || ""}`.trim();
      } else {
        ingredient.scaledQuantity = ingredient.quantity; // Keep unchanged if not numeric
      }
    }
  });
}

// Generate the thumbnail URL for an ingredient
function getIngredientThumbnail(ingredientName: string): string {
  const formattedName = ingredientName.split(" (")[0].trim(); // Remove extra details
  return `https://www.themealdb.com/images/ingredients/${formattedName}.png`;
}

// Set a default thumbnail for ingredients without a valid image
function setDefaultThumbnail(event: Event): void {
  const target = event.target as HTMLImageElement;
  target.src = require("@/assets/grocery.png"); // Fallback image
}

// Fetch the shopping list when the component is mounted
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

/* Warn about skipped meals */
.skipped-meals-warning {
  margin: 20px 0;
  padding: 15px;
  background-color: #fff3cd; /* Light yellow background */
  border: 1px solid #ffeeba; /* Light yellow border */
  border-radius: 8px;
  color: #856404;
}

/* Each meal “card” */
.meal-group {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #cce7ff;
  border-radius: 8px;
  background-color: #f1faff;
}

.meal-group h3 {
  margin-bottom: 10px;
  font-size: 1.2rem;
  color: #00509e; /* deeper blue */
}

.meal-scaling {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.meal-scaling label {
  margin-right: 10px;
  font-size: 1rem;
  color: #555;
}

.meal-scaling input {
  padding: 8px;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 100px;
  text-align: center;
}
.checked {
  text-decoration: line-through;
  color: #999;
}
</style>
