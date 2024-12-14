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
import api from "@/api"; // Import your Axios instance
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
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

.scaling-input {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
}

.scaling-input input {
  margin-left: 10px;
  width: 100px;
  text-align: center;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin: 5px 0;
}

button {
  display: block;
  margin: 0 auto 20px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
