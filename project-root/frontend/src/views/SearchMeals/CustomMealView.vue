<template>
  <div class="view-meal">
    <!-- Meal Header Section -->
    <div class="meal-header">
      <!-- Thumbnail Section -->
      <div class="thumbnail-section">
        <img
          :src="meal.thumbnail"
          alt="Meal Thumbnail"
          class="meal-thumbnail"
        />
        <p class="meal-category">{{ meal.name }}</p>
        <p class="meal-category">Category: {{ meal.category }}</p>
      </div>

      <!-- Ingredients Section -->
      <div class="meal-ingredients">
        <h2>Ingredients</h2>
        <ul>
          <li
            v-for="ingredient in meal.ingredients"
            :key="ingredient"
            class="ingredient-item"
          >
            {{ ingredient }}
          </li>
        </ul>
      </div>
    </div>

    <!-- Instructions Section -->
    <div class="meal-instructions">
      <h2>Instructions</h2>
      <p>{{ meal.instructions }}</p>
    </div>

    <!-- Action Buttons -->
    <div class="actions">
      <button @click="goBack">
        <font-awesome-icon :icon="['fas', 'arrow-left']" /> Back
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const mealId = route.params.id; // Custom meal ID from route params

// Meal data structure
const meal = ref({
  id: "",
  name: "",
  thumbnail: "",
  ingredients: [],
  instructions: "",
  category: "",
});

// Fetch custom meal details on mount
onMounted(async () => {
  try {
    const response = await api.get(`/api/meals/${mealId}`);
    console.log(response);
    const data = response.data;

    // Map API data to local meal structure
    meal.value = {
      id: data.id,
      name: data.name,
      thumbnail: data.thumbnail || "default-thumbnail.jpg",
      ingredients: data.ingredients.split(", "), // Assuming ingredients are stored as a string
      instructions: data.instructions || "No instructions available.",
      category: data.category || "Unknown",
    };
  } catch (error) {
    console.error("Error fetching custom meal details:", error);
    alert("Failed to fetch meal details.");
    router.push("/"); // Redirect back to the custom meal list if there's an error
  }
});

// Navigate back to the previous page
function goBack() {
  router.push("/");
}
</script>

<style scoped>
/* Add styles similar to ViewMeal.vue */
.view-meal {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}
.meal-header {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  align-items: flex-start;
}
.thumbnail-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}
.meal-thumbnail {
  width: 250px;
  height: 250px;
  border-radius: 10px;
  object-fit: cover;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}
.meal-category {
  font-size: 1.1rem;
  color: #333;
  font-weight: bold;
  margin-bottom: 10px;
}
.meal-ingredients {
  flex: 1;
}
.meal-ingredients h2 {
  margin: 0 0 10px;
  color: #333;
}
.ingredient-item {
  margin-bottom: 10px;
  font-size: 1rem;
  color: #555;
}
.meal-instructions {
  margin-top: 20px;
  font-size: 1rem;
  line-height: 1.5;
  text-align: justify;
  color: #444;
  background-color: #f7faff;
  padding: 15px;
  border-radius: 8px;
}
.meal-instructions h2 {
  margin-bottom: 10px;
  color: #333;
}
.actions {
  margin-top: 20px;
  display: flex;
  gap: 15px;
  justify-content: center;
}
.actions button {
  padding: 10px 20px;
  font-size: 1rem;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  background-color: #ffc107;
  color: white;
  transition: all 0.3s ease;
}
.actions button:hover {
  background-color: #e0a800;
}
</style>
