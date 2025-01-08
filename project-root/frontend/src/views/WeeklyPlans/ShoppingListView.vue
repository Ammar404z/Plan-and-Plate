<template>
  <div class="shopping-list-view">
    <h1>Shopping List</h1>
    <div class="scaling-input">
      <label for="scaling">Scaling Factor:</label>
      <input
        id="scaling"
        type="number"
        min="1"
        v-model.number="scalingFactor"
        placeholder="Enter scaling factor"
      />
    </div>
    <button @click="generateShoppingList(route.query.planId)">
      Generate Shopping List
    </button>
    <ul>
      <li v-for="(quantity, ingredient) in shoppingList" :key="ingredient">
        {{ ingredient }}: {{ quantity }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const shoppingList = ref<Record<string, string | number>>({});
const scalingFactor = ref<number>(1); // Default scaling factor
const route = useRoute();

async function generateShoppingList(planId: number | string) {
  if (!scalingFactor.value || scalingFactor.value < 1) {
    alert("Please enter a valid scaling factor (greater than or equal to 1).");
    return;
  }
  try {
    const response = await api.get(`/api/shopping-list/${planId}`, {
      params: { multiplier: scalingFactor.value }, // Pass scaling factor as a query parameter
    });
    console.log("Generated Shopping List:", response.data);
    shoppingList.value = response.data;
  } catch (error) {
    console.error("Error generating shopping list:", error);
    alert("Failed to generate shopping list. Please try again.");
  }
}

onMounted(async () => {
  try {
    const response = await api.get(`/api/shopping-list/${route.query.planId}`, {
      params: { multiplier: scalingFactor.value }, // Fetch with default scaling factor
    });
    shoppingList.value = response.data;
  } catch (error) {
    console.error("Failed to fetch shopping list:", error);
  }
});
</script>

<style scoped>
.shopping-list-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9; /* Light background color */
  border-radius: 8px; /* Rounded corners */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* Subtle shadow for depth */
}

/* Styling for the header */
h1 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: bold;
  color: #333;
}

/* Styling for the scaling input section */
.scaling-input {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.scaling-input label {
  font-size: 1rem;
  margin-right: 10px;
  color: #555;
}

.scaling-input input {
  padding: 8px;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 100px;
  text-align: center;
}

/* Styling for the list of ingredients */
ul {
  list-style-type: none;
  padding: 0;
  margin: 20px 0;
  font-size: 1rem;
  color: #555;
}

li {
  margin: 8px 0;
  padding: 8px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); /* Light shadow for each list item */
}

/* Styling for the generate button */
button {
  display: block;
  margin: 20px auto;
  padding: 12px 24px;
  font-size: 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

button:hover {
  background-color: #0056b3;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* Add depth on hover */
}

button:active {
  background-color: #004494;
  box-shadow: none; /* Remove shadow on click */
}
</style>
