<template>
  <div class="statistics-view">
    <h1>Statistics</h1>

    <!-- Category Statistics -->
    <h2>Saved Meals by Category</h2>
    <canvas id="categoryChart"></canvas>

    <!-- Total Saved Count -->
    <div>
      <h2>Total Recipes Saved</h2>
      <p><strong>Total Saves:</strong> {{ totalSavedCount }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import api from "@/api"; // Your Axios instance
import Chart from "chart.js/auto";
import { onMounted, ref } from "vue";

const categoryDistribution = ref({});
const chartInstance = ref(null);
const totalSavedCount = ref(0);

onMounted(async () => {
  try {
    // Fetch the category distribution
    const distributionResponse = await api.get(
      "/api/statistics/category-distribution"
    );
    categoryDistribution.value = distributionResponse.data;

    // Create Pie Chart
    const ctx = document.getElementById("categoryChart").getContext("2d");
    const categories = Object.keys(categoryDistribution.value);
    const counts = Object.values(categoryDistribution.value);

    chartInstance.value = new Chart(ctx, {
      type: "pie",
      data: {
        labels: categories,
        datasets: [
          {
            label: "Saved Meals by Category",
            data: counts,
            backgroundColor: [
              "#FF6384",
              "#36A2EB",
              "#FFCE56",
              "#4BC0C0",
              "#9966FF",
              "#FF9F40",
            ],
          },
        ],
      },
      options: {
        responsive: true,
      },
    });

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

canvas {
  margin: 20px auto;
  max-width: 600px;
}
</style>
