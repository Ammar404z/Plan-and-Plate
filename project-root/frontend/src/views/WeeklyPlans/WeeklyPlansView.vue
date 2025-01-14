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

        <!-- Generate Shopping List Button -->
        <button @click="navigateToShoppingList(plan.id)">
          Generate Shopping List
        </button>

        <!-- Edit Plan Button -->
        <button @click="navigateToEditPlan(plan.id)">Edit Plan</button>
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

// Function to navigate to the Shopping List View
function navigateToShoppingList(planId: number) {
  router.push({
    path: `/shopping-list/${planId}`, // Include `planId` in the path
  });
}

function navigateToEditPlan(planId: number) {
  router.push(`/create-weekly-plans/${planId}`);
}

// Fetch data on component mount
onMounted(async () => {
  await fetchMeals();
  await fetchWeeklyPlans();
});
</script>

<style scoped>
/* General container styling for the view */
.weekly-plans-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
  color: #333;
}

/* Section title styling */
h1 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 2rem;
  color: #007bff;
}

/* Styling for each weekly plan */
.weekly-plan {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.weekly-plan h2 {
  margin-bottom: 10px;
  font-size: 1.5rem;
  color: #0056b3;
}

/* Styling for meal list */
.weekly-plan ul {
  list-style: none;
  padding: 0;
  margin: 0 0 15px;
}

.weekly-plan li {
  font-size: 1rem;
  padding: 5px 0;
  color: #555;
  border-bottom: 1px solid #eee;
}

.weekly-plan li:last-child {
  border-bottom: none;
}

/* Button styling */
button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 5px;
  margin: 5px;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #0056b3;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* Create button container */
.create-button {
  margin-bottom: 30px;
  text-align: center;
}

.create-button button {
  font-size: 1.2rem;
  padding: 10px 20px;
}

/* Message for no plans available */
p {
  text-align: center;
  font-size: 1.1rem;
  color: #777;
  margin-top: 20px;
}

/* Responsive design */
@media (max-width: 768px) {
  .weekly-plan {
    padding: 10px;
  }

  button {
    font-size: 0.9rem;
    padding: 8px 10px;
  }
}
</style>
