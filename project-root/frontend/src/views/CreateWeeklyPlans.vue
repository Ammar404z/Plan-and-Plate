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
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

.week-selector {
  margin-bottom: 20px;
}

.days {
  display: flex;
  justify-content: space-between;
  flex-wrap: nowrap; /* Horizontal layout */
  gap: 10px; /* Add spacing between days */
}

.day {
  flex: 1;
  min-width: 100px;
}

.meal-select {
  margin-top: 5px;
  width: 100%;
}
</style>
