<template>
  <div class="weekly-plan">
    <h1>Weekly Plan</h1>

    <!-- Week Selector -->
    <div class="week-selector">
      <label for="week">Select Week:</label>
      <select id="week" v-model="selectedWeek" class="week-select">
        <option v-for="week in weeks" :key="week" :value="week">
          Week {{ week }}
        </option>
      </select>
    </div>

    <!-- Days Selector -->
    <div class="days">
      <div v-for="day in days" :key="day" class="day">
        <label :for="`meal-${day}`">{{ day }}</label>
        <select
          :id="`meal-${day}`"
          v-model="selectedMeals[day]"
          class="meal-select"
        >
          <option v-for="meal in savedMeals" :key="meal.id" :value="meal.id">
            {{ meal.name }}
          </option>
        </select>
      </div>
    </div>

    <!-- Save Plan Button -->
    <button @click="saveWeeklyPlan">Save Plan</button>
  </div>
</template>

<script setup lang="ts">
import api from "@/api"; // Axios instance
import router from "@/router";
import { onMounted, ref } from "vue";

interface Meal {
  id: number;
  name: string;
}

interface SelectedMeals {
  [key: string]: number | null;
}

const days: string[] = [
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
  "Sunday",
];

const weeks: number[] = [1, 2, 3, 4]; // Hardcoded weeks 1 to 4
const savedMeals = ref<Meal[]>([]);
const selectedMeals = ref<SelectedMeals>(
  days.reduce((acc, day) => {
    acc[day] = null;
    return acc;
  }, {} as SelectedMeals)
);
const selectedWeek = ref<number | null>(null);

onMounted(async (): Promise<void> => {
  try {
    const response = await api.get<Meal[]>("/api/meals");
    savedMeals.value = response.data;
  } catch (error) {
    console.error("Error loading saved meals:", error);
  }
});

async function saveWeeklyPlan(): Promise<void> {
  if (!selectedWeek.value) {
    alert("Please select a week before saving the plan.");
    return;
  }

  try {
    const response = await api.post("/api/create-weekly-plans", {
      week: selectedWeek.value,
      meals: selectedMeals.value,
    });
    alert("Weekly plan saved successfully!");
    console.log(response.data);
    router.push("/view-weekly-plans");
  } catch (error: any) {
    if (error.response && error.response.status === 400) {
      alert("Error: " + error.response.data);
    } else {
      console.error("Unexpected error:", error);
      alert("An unexpected error occurred.");
    }
  }
}
</script>
<style scoped>
.weekly-plan {
  max-width: 800px;
  margin: 40px auto;
  padding: 30px;
  background-color: #f9f9f9; /* Light background for the plan container */
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Soft shadow for depth */
  text-align: center;
  font-family: Arial, sans-serif;
}

.weekly-plan h1 {
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
  font-weight: bold;
}

.week-selector {
  margin-bottom: 30px;
}

.week-selector label {
  font-size: 1rem;
  font-weight: bold;
  margin-right: 10px;
  color: #555;
}

.week-select {
  font-size: 1rem;
  padding: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #fff;
  transition: border-color 0.3s ease;
}

.week-select:focus {
  border-color: #007bff;
  outline: none;
}

.days {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 15px;
}

.day {
  flex: 1 1 calc(33.333% - 15px); /* Allows up to 3 items per row */
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 15px;
}

.day label {
  font-size: 0.9rem;
  margin-bottom: 5px;
  color: #555;
}

.meal-select {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #fff;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.meal-select:focus {
  border-color: #007bff;
  outline: none;
}

button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  font-size: 1rem;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #0056b3;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .day {
    flex: 1 1 100%; /* Stack items on smaller screens */
  }

  .weekly-plan {
    padding: 20px;
  }
}
</style>
