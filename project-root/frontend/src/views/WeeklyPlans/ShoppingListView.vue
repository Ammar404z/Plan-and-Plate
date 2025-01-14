<template>
  <div class="shopping-list-view">
    <h1>Shopping List</h1>

    <!-- Show skipped meals warning if any -->
    <div v-if="skippedMeals.length > 0" class="skipped-meals-warning">
      <p>Some meals were not found and have been skipped:</p>
      <ul>
        <li v-for="day in skippedMeals" :key="day">{{ day }}</li>
      </ul>
    </div>

    <div v-if="shoppingList.length > 0">
      <div v-for="meal in shoppingList" :key="meal.mealName" class="meal-group">
        <h3>{{ meal.day }} - {{ meal.mealName }}</h3>
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
        <ul>
          <li
              v-for="ingredient in meal.ingredients"
              :key="ingredient.name"
          >
            {{ ingredient.name }}: {{ ingredient.scaledQuantity }}
          </li>
        </ul>
      </div>
    </div>
    <p v-else>Loading shopping list or no meals available...</p>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

interface Ingredient {
  name: string;
  quantity: string;
  scaledQuantity: string;
}

interface Meal {
  day: string;
  mealName: string;
  scalingFactor: number;
  ingredients: Ingredient[];
}

const shoppingList = ref<Meal[]>([]);
const skippedMeals = ref<string[]>([]);
const route = useRoute();

// Fetch the shopping list grouped by meals
async function fetchShoppingList() {
  try {
    const planId = route.params.planId;
    if (!planId) {
      console.error("Plan ID is undefined. Cannot fetch shopping list.");
      return;
    }

    const response = await api.get(`/api/shopping-list/${planId}`);
    console.log("Backend Response:", response.data);

    // Extract any skipped meals if present in the first entry
    if (response.data[0]?.skippedMeals) {
      skippedMeals.value = response.data[0].skippedMeals;
    }

    // Map the backend data
    shoppingList.value = response.data.map((meal: any) => ({
      ...meal,
      ingredients: Object.entries(meal.ingredients).map(([name, quantity]) => ({
        name,
        quantity,
        scaledQuantity: quantity,
      })),
    }));
  } catch (error) {
    console.error("Failed to fetch shopping list:", error);
  }
}

// Update ingredient quantities when the scaling factor changes
function updateMealScaling(meal: any) {
  meal.ingredients.forEach((ingredient: any) => {
    if (ingredient.quantity.includes("original measurement")) {
      // Update the scaled quantity to reflect the scaling factor
      ingredient.scaledQuantity = `${meal.scalingFactor}x original measurement`;
    } else {
      // Try to parse the numeric + unit parts
      const [amount, unit] = ingredient.quantity.split(" ");
      if (!isNaN(parseFloat(amount))) {
        ingredient.scaledQuantity = `${(
            parseFloat(amount) * meal.scalingFactor
        ).toFixed(2)} ${unit || ""}`.trim();
      } else {
        // If no numeric part, keep it unchanged
        ingredient.scaledQuantity = ingredient.quantity;
      }
    }
  });
}

onMounted(() => {
  fetchShoppingList();
});
</script>

<style scoped>
.shopping-list-view {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff; /* White “card” container */
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.shopping-list-view h1 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
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
</style>
