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
          <li v-for="ingredient in meal.ingredients" :key="ingredient.name">
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
const skippedMeals = ref<string[]>([]); //  ref for skipped meals
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

    // Extract skipped meals if present in the first meal
    if (response.data[0]?.skippedMeals) {
      skippedMeals.value = response.data[0].skippedMeals;
    }

    // Map the backend data as before
    shoppingList.value = response.data.map((meal: any) => ({
      ...meal,
      ingredients: Object.entries(
        meal.ingredients as Record<string, string>
      ).map(([name, quantity]: [string, string]) => ({
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
    // Check if the quantity contains "original measurement"
    if (ingredient.quantity.includes("original measurement")) {
      // Update the scaled quantity to reflect the scaling factor
      ingredient.scaledQuantity = `${meal.scalingFactor}x original measurement`;
    } else {
      // Split the quantity into numeric and unit parts if possible
      const [amount, unit] = ingredient.quantity.split(" ");
      if (!isNaN(parseFloat(amount))) {
        // Scale the numeric value and append the unit
        ingredient.scaledQuantity = `${(
          parseFloat(amount) * meal.scalingFactor
        ).toFixed(2)} ${unit || ""}`.trim();
      } else {
        // If splitting fails, retain the original quantity
        ingredient.scaledQuantity = ingredient.quantity;
      }
    }
  });
}

// Fetch shopping list on mount
onMounted(() => {
  fetchShoppingList();
});
</script>

<style scoped>
.meal-group {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.meal-group h3 {
  margin-bottom: 10px;
  font-size: 1.2rem;
  color: #333;
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

.skipped-meals-warning {
  margin: 20px 0;
  padding: 15px;
  background-color: #fff3cd;
  border: 1px solid #ffeeba;
  border-radius: 8px;
  color: #856404;
}

.skipped-meals-warning ul {
  margin: 10px 0 0 20px;
  padding: 0;
}
</style>
