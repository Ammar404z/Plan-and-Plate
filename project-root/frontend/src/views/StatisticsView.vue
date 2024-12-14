<template>
  <div class="statistics-view">
    <h1>Statistics</h1>

    <!-- Top 5 Most Saved Recipes -->
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

    <!-- Total Saved Count -->
    <div>
      <h2>Total Recipes Saved</h2>
      <p><strong>Total Saves:</strong> {{ totalSavedCount }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import api from "@/api"; // Your Axios instance
import { onMounted, ref } from "vue";

const topSavedMeals = ref([]);
const totalSavedCount = ref(0);

onMounted(async () => {
  try {
    // Fetch top saved meals
    const topSavedResponse = await api.get("/api/statistics/top-saved-recipes");
    topSavedMeals.value = topSavedResponse.data.slice(0, 5);

    // Fetch total saved count
    const totalSavedResponse = await api.get(
      "/api/statistics/total-saved-count"
    );
    totalSavedCount.value = totalSavedResponse.data;
  } catch (error) {
    console.error("Error fetching statistics:", error);
  }
});
</script>

<style scoped>
.statistics-view {
  max-width: 700px;
  margin: 0 auto;
  padding: 30px;
  text-align: center;
  background-color: #f5f5f5; /* Light background */
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* Subtle shadow for depth */
}

.statistics-view h1 {
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333; /* Darker text for contrast */
  font-weight: bold;
}

.statistics-view h2 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #555; /* Slightly lighter than the main heading */
}

ul {
  list-style-type: none; /* Remove bullet points */
  padding: 0;
}

li {
  text-align: left;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

li p {
  margin: 5px 0;
  font-size: 16px;
  line-height: 1.5;
}

li p strong {
  color: #333;
}

.stat-card {
  padding: 20px;
  margin-top: 30px;
  border-radius: 8px;
  background-color: #e8f7ff; /* Light blue background */
  border: 1px solid #cceeff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Subtle shadow */
}

.stat-card h2 {
  font-size: 1.25rem;
  margin-bottom: 10px;
  color: #0077cc; /* Blue for emphasis */
}

.stat-card p {
  font-size: 16px;
  margin: 0;
}

.statistics-view p {
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .statistics-view {
    padding: 20px;
    margin: 10px;
  }

  li {
    padding: 10px;
  }

  h1,
  h2 {
    font-size: 1.5rem;
  }

  li p {
    font-size: 14px;
  }
}
</style>
