<template>
  <div class="weekly-plan">
    <h1>Weekly Plan</h1>

    <!-- Week Selector -->
    <div class="week-selector">
      <label for="week">Select Week:</label>
      <select id="week" v-model="selectedWeek" class="week-select">
        <!-- Dynamically populates the week options -->
        <option v-for="week in weeks" :key="week" :value="week">
          Week {{ week }}
        </option>
      </select>
    </div>

    <!-- Days Selector -->
    <div class="days">
      <!-- Dynamically generates a day selector for each day of the week -->
      <div v-for="day in days" :key="day" class="day">
        <label :for="`meal-${day}`">{{ day }}</label>
        <select
          :id="`meal-${day}`"
          v-model="selectedMeals[day].mealId"
          class="meal-select"
        >
          <option v-for="meal in savedMeals" :key="meal.id" :value="meal.id">
            {{ meal.name }}
          </option>
        </select>

        <label :for="`portion-${day}`">Portion Size:</label>
        <input
          required
          type="number"
          :id="`portion-${day}`"
          v-model="selectedMeals[day].portionSize"
          class="portion-size-input"
          min="1"
          placeholder="Enter portion size"
        />
      </div>
    </div>

    <!-- Save Plan Button -->
    <button @click="saveWeeklyPlan">Save Plan</button>
    <!-- Cancel Button -->
    <button @click="cancel">Cancel</button>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import router from "@/router";
import { onMounted, ref } from "vue";

// Interface for Meal objects
interface Meal {
  id: number;
  name: string;
}

// Interface for mapping days to selected meal IDs
interface SelectedMeals {
  [key: string]: {
    mealId: number | null; // Meal ID or null if not selected
    portionSize: number; // Portion size, default to 1
  };
}

// List of days of the week
const days: string[] = [
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
  "Sunday",
];

// Predefined weeks available for selection
const weeks: number[] = [1, 2, 3, 4];

// Reactive variables
const savedMeals = ref<Meal[]>([]); // Array to store meals fetched from the API
const selectedMeals = ref<SelectedMeals>(
  days.reduce((acc, day) => {
    acc[day] = { mealId: null, portionSize: 1 }; // Initialize each day
    return acc;
  }, {} as SelectedMeals)
);
const selectedWeek = ref<number | null>(null); // Selected week, initially null

// Lifecycle hook: Fetch meals when the component is mounted
onMounted(async (): Promise<void> => {
  try {
    const response = await api.get<Meal[]>("/api/meals"); // API call to fetch meals
    savedMeals.value = response.data; // Store the fetched meals
  } catch (error) {
    console.error("Error loading saved meals:", error); // Log errors if any
  }
});

// Function to save the weekly plan
async function saveWeeklyPlan(): Promise<void> {
  if (!selectedWeek.value) {
    alert("Please select a week before saving the plan.");
    return;
  }

  const hasMeals = Object.values(selectedMeals.value).some(
    (meal) => meal.mealId !== null
  );
  if (!hasMeals) {
    alert("Please assign at least one meal before saving the plan.");
    return;
  }

  try {
    const payload = {
      week: selectedWeek.value,
      meals: Object.entries(selectedMeals.value)
        .filter(([day, data]) => data.mealId !== null) // Only include days with selected meals
        .reduce((acc, [day, data]) => {
          acc[day] = data.mealId; // Map of day to mealId
          return acc;
        }, {}),
      portionSizes: Object.entries(selectedMeals.value)
        .filter(([day, data]) => data.mealId !== null) // Only include days with selected meals
        .reduce((acc, [day, data]) => {
          acc[day] = data.portionSize; // Map of day to portion size
          return acc;
        }, {}),
    };
    await api.post("/api/create-weekly-plans", payload);
    alert("Weekly plan saved successfully!");
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

// Function to cancel and navigate back to the weekly plans view
function cancel() {
  router.push("/view-weekly-plans");
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
.portion-size-input {
  width: 20%;
  padding: 8px 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.portion-size-input:focus {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.5);
  outline: none;
}

.portion-size-input::placeholder {
  color: #999;
  font-style: italic;
}

.portion-size-input:disabled {
  background-color: #f5f5f5;
  border-color: #ddd;
  cursor: not-allowed;
}
</style>
