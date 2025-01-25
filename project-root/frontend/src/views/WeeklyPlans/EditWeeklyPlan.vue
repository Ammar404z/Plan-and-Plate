<template>
  <div class="edit-weekly-plan">
    <h1>Edit Weekly Plan</h1>

    <!-- Form for editing meals by day -->
    <form @submit.prevent="savePlan">
      <div
        v-for="(mealId, day) in updatedMeals"
        :key="day"
        class="day-meal-entry"
      >
        <label :for="day">{{ day }}</label>
        <!-- Meal selection dropdown -->
        <select v-model="updatedMeals[day]" :id="day">
          <option value="">-- No Meal --</option>
          <!-- Allow no meal -->
          <option v-for="meal in meals" :key="meal.id" :value="meal.id">
            {{ meal.name }}
          </option>
        </select>

        <!-- Portion size input -->
        <input
          required
          v-model.number="portionSizes[day]"
          type="number"
          min="1"
          placeholder="Portion Size"
          :disabled="!updatedMeals[day]"
        />
      </div>
      <!-- Action buttons -->
      <div class="actions">
        <button type="submit">Save</button>
        <button type="button" @click="cancelEdit">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import api from "@/api";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const planId = route.params.planId;

const updatedMeals = ref({});
const portionSizes = ref({});
const meals = ref([]);
const plan = ref({});
const allDays = [
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
  "Sunday",
];

// Fetch the weekly plan and available meals
const fetchPlanAndMeals = async () => {
  try {
    const planResponse = await api.get(`/api/create-weekly-plans/${planId}`);
    plan.value = planResponse.data;

    // Initialize updatedMeals and portionSizes
    updatedMeals.value = {};
    portionSizes.value = {};
    allDays.forEach((day) => {
      updatedMeals.value[day] = plan.value.meals[day] || ""; // Use existing meal or leave it empty
      portionSizes.value[day] = plan.value.portionSizes[day] || null; // Use existing portion size or null
    });

    const mealsResponse = await api.get("/api/meals");
    meals.value = mealsResponse.data;
  } catch (error) {
    console.error("Error fetching plan or meals:", error);
  }
};

// Save the updated weekly plan
const savePlan = async () => {
  try {
    const payload = {
      meals: updatedMeals.value,
      portionSizes: portionSizes.value,
    };

    await api.put(`/api/create-weekly-plans/${planId}`, payload);

    alert("Weekly plan updated successfully!");
    router.push("/view-weekly-plans");
  } catch (error) {
    console.error("Error saving weekly plan:", error);
    alert("Failed to save the weekly plan. Please try again.");
  }
};

// Cancel editing and go back to weekly plans
const cancelEdit = () => {
  router.push("/view-weekly-plans");
};

// Fetch data on component mount
onMounted(fetchPlanAndMeals);
</script>

<style scoped>
.edit-weekly-plan {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

form {
  display: flex;
  flex-direction: column;
}

.day-meal-entry {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

input[type="number"] {
  width: 80px;
  margin-left: 10px;
}

label {
  flex: 1;
  margin-right: 10px;
}

select {
  flex: 2;
  padding: 5px;
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

button {
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button[type="submit"] {
  background-color: #4caf50;
  color: white;
}

button[type="button"] {
  background-color: #f44336;
  color: white;
}
</style>
