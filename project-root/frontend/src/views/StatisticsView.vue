<template>
  <div class="statistics-view">
    <h1>Statistics</h1>

    <div v-if="topSavedMeals.length">
      <h2>Top 5 Most Saved Recipes</h2>
      <ul>
        <li v-for="meal in topSavedMeals" :key="meal.id">
          <p><strong>Name:</strong> {{ meal.name }}</p>
          <p><strong>Times Saved:</strong> {{ meal.savedCount }}</p>
        </li>
      </ul>
    </div>

    <div v-else>
      <p>No data available for the most saved recipes.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import api from "@/api"; // Your Axios instance
import { onMounted, ref } from "vue";

const topSavedMeals = ref([]);

onMounted(async () => {
  try {
    const response = await api.get("/api/statistics/top-saved-recipes");
    topSavedMeals.value = response.data.slice(0, 5);
  } catch (error) {
    console.error("Error fetching the most saved recipe:", error);
  }
});
</script>

<style scoped>
.statistics-view {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

.stat-card {
  border: 1px solid #ccc;
  padding: 15px;
  margin-top: 20px;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.stat-card h2 {
  margin-bottom: 10px;
}

.stat-card p {
  margin: 5px 0;
  font-size: 16px;
}
</style>
