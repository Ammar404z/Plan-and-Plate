<template>
  <div class="weekly-plans-container">
    <!-- Title with a box -->
    <div class="title-box">
      <h1>Weekly Plans</h1>
    </div>

    <!-- Create Meal Plan Button -->
    <div class="create-button">
      <button @click="createWeeklyPlan" class="action-button">
        Create Meal Plan
      </button>
    </div>

    <!-- Weekly Plans List -->
    <div v-if="weeklyPlans.length > 0">
      <div v-for="plan in weeklyPlans" :key="plan.id" class="weekly-plan">
        <h2>Week {{ plan.week }}</h2>
        <ul>
          <li v-for="(mealId, day) in plan.meals" :key="day">
            {{ day }}: {{ getMealName(mealId) }}
            <span v-if="plan.portionSizes[day]">
              | Portion Size: {{ plan.portionSizes[day] }}
            </span>
          </li>
        </ul>
        <!-- Buttons for each plan -->
        <div class="button-container">
          <button @click="deletePlan(plan.id)" class="action-button">
            Delete Plan
          </button>
          <button
            @click="navigateToShoppingList(plan.id)"
            class="action-button"
          >
            Generate Shopping List
          </button>
          <button @click="navigateToEditPlan(plan.id)" class="action-button">
            Edit Plan
          </button>
        </div>
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
  portionSizes: Record<string, number>;
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
    weeklyPlans.value = response.data.map((plan: WeeklyPlan) => ({
      ...plan,
      portionSizes: plan.portionSizes || {}, // Ensure portionSizes exists
    }));
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
  display: flex;
  flex-direction: column;
  align-items: center; /* Centers the content horizontally */
  max-width: 800px;
  margin: 0 auto;
}

/* Title Box Styling */
.title-box {
  font-size: 1.2rem;
  padding: 15px 20px;
  background-color: #e3f6e8; /* Lighter green background */
  color: #28a745; /* Green text */
  border: 2px solid #89cff0; /* Similar to dropdown */
  border-radius: 10px; /* Similar to dropdown */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Similar to dropdown */
  display: flex;
  justify-content: center;
  align-items: center;
  width: 250px; /* Match the Add Custom Meal width */
  margin: 0 auto 10px auto; /* Center and add spacing below */
}

.title-box h1 {
  font-size: 1.2rem;
  margin: 0;
  color: #28a745;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  text-align: center;
}

/* Styling for each weekly plan */
.weekly-plan {
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 10px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  width: 100%;
}

.weekly-plan h2 {
  margin-bottom: 10px;
  font-size: 1rem;
  color: #28a745;
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

/* Button Styling */
.action-button {
  background-color: #e3f6e8; /* Light green background */
  color: #28a745; /* Green text */
  border: 2px solid #89cff0;
  border-radius: 10px;
  padding: 10px 20px; /* Uniform padding */
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 5px; /* Space between buttons */
  width: 200px; /* Set uniform width for all buttons */
  text-align: center;
  display: inline-block;
}

.action-button:hover {
  background-color: #218838; /* Darker green */
  color: white; /* White text */
  transform: translateY(-2px); /* Lift effect */
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
  padding: 15px 20px;
  font-weight: bold; /* Add this to make the font thick (bold) */
  background-color: #e3f6e8; /* Light green background */
  color: #28a745; /* Green text */
  border: 2px solid #89cff0; /* Border matching dropdown buttons */
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
  text-align: center;
  width: 290px; /* Uniform width */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Message for no plans available */
p {
  text-align: center;
  font-size: 1.2rem;
  color: #777;
  margin-top: 20px;
}

/* Responsive design */
@media (max-width: 768px) {
  .weekly-plan {
    padding: 10px;
  }

  .action-button {
    font-size: 0.9rem;
    padding: 8px 10px;
    width: 100%; /* Full width on smaller screens */
  }
}
</style>
