<template>
  <div>
    <h1>Weekly Plans</h1>

    <!-- Create Meal Plan Button -->
    <div class="create-button">
      <button @click="createWeeklyPlan">Create Meal Plan</button>
    </div>

    <div v-if="weeklyPlans.length > 0">
      <div v-for="plan in weeklyPlans" :key="plan.id" class="weekly-plan">
        <h2>Week {{ plan.week }}</h2>
        <ul>
          <li v-for="(mealId, day) in plan.meals" :key="day">
            {{ day }}: {{ getMealName(mealId) }}
          </li>
        </ul>
        <!-- Delete Plan Button -->
        <button @click="deletePlan(plan.id)">Delete Plan</button>
      </div>
    </div>
    <p v-else>No weekly plans available.</p>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

interface WeeklyPlan {
  id: number;
  week: number;
  meals: Record<string, number>;
}

interface Meal {
  id: number;
  name: string;
}

const weeklyPlans = ref<WeeklyPlan[]>([]);
const meals = ref<Meal[]>([]);
const router = useRouter();

// Function to fetch weekly plans
async function fetchWeeklyPlans() {
  try {
    const response = await api.get("/api/create-weekly-plans");
    weeklyPlans.value = response.data;
  } catch (error) {
    console.error("Error fetching weekly plans:", error);
  }
}

// Function to fetch meals
async function fetchMeals() {
  try {
    const response = await api.get("/api/meals");
    meals.value = response.data;
  } catch (error) {
    console.error("Error fetching meals:", error);
  }
}

// Function to get meal name by ID
function getMealName(mealId: number): string {
  const meal = meals.value.find((meal) => meal.id === mealId);
  return meal ? meal.name : "Unknown Meal";
}

// Function to delete a weekly plan
async function deletePlan(planId: number) {
  try {
    await api.delete(`/api/create-weekly-plans/${planId}`);
    // Remove the deleted plan from the list
    weeklyPlans.value = weeklyPlans.value.filter((plan) => plan.id !== planId);
    alert("Plan deleted successfully!");
  } catch (error) {
    console.error("Error deleting plan:", error);
    alert("An error occurred while deleting the plan.");
  }
}

// Function to navigate to the create meal plan page
function createWeeklyPlan() {
  router.push("/create-weekly-plans");
}

// Fetch data on component mount
onMounted(async () => {
  await fetchMeals();
  await fetchWeeklyPlans();
});
</script>

<style scoped>
.weekly-plan {
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.create-button {
  margin-bottom: 20px;
  text-align: center;
}

button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  border-radius: 5px;
}

button:hover {
  background-color: #0056b3;
}
</style>
