<template>
  <div class="search-container">
    <h1>Recipe Search</h1>
    <input
      v-model="query"
      placeholder="Search for a recipe"
      @input="searchMeals"
      class="search-input"
    />

    <!-- Wrapper element with v-if applied -->
    <div v-if="meals.length > 0" class="results">
      <div class="meal-card" v-for="meal in meals" :key="meal.idMeal">
        <img :src="meal.strMealThumb" alt="Meal Image" class="meal-image" />
        <div class="meal-info">
          <h3 class="meal-title">{{ meal.strMeal }}</h3>
          <p class="meal-instructions">
            {{ meal.strInstructions.substring(0, 100) }}...
          </p>
          <!-- Save Recipe Button -->
          <button @click="saveRecipe(meal)">Save Recipe</button>
        </div>
      </div>
    </div>

    <!-- Show "No results found" if query is entered and no meals are found -->
    <p v-else-if="query && meals.length === 0" class="no-results">
      No results found.
    </p>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import { ref } from "vue";

//Define the structure of a Meal from TheMealDB API
interface Meal {
  idMeal: string;
  strMeal: string;
  strMealThumb: string;
  strInstructions: string;
  [key: string]: any; // we do this to allow dynamic ingredient keys like strIngredient1, strMeasure1, etc...
}

const query = ref("");
const meals = ref<Meal[]>([]);

async function searchMeals() {
  if (query.value) {
    try {
      const response = await api.get("/api/meals/search", {
        params: { name: query.value },
      });
      meals.value = response.data.meals || [];
    } catch (error) {
      console.error("Error fetching meals:", error);
    }
  } else {
    meals.value = [];
  }
}

function extractIngredients(meal: Meal): string {
  const ingredients: string[] = [];
  for (let i = 1; i <= 20; i++) {
    const ingredient = meal[`strIngredient${i}`];
    const measure = meal[`strMeasure${i}`];
    if (ingredient && ingredient.trim() !== "") {
      ingredients.push(`${ingredient} - ${measure || "as needed"}`);
    }
  }
  return ingredients.join(", ");
}

async function saveRecipe(meal: Meal) {
  const recipe = {
    name: meal.strMeal || "Unnamed Recipe",
    ingredients: extractIngredients(meal),
    instructions: meal.strInstructions,
    thumbnail: meal.strMealThumb,
  };

  console.log("Payload being sent:", recipe); // Debug log

  try {
    const response = await api.post("/api/meals/add", recipe);
    alert(`Recipe "${response.data.name}" saved successfully.`);
  } catch (error) {
    console.error("Error saving recipe:", error);
  }
}
</script>

<style scoped>
/* Container Styling */
.search-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

/* Heading Styling */
h1 {
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

/* Search Input Styling */
.search-input {
  padding: 10px;
  width: 100%;
  max-width: 400px;
  border-radius: 5px;
  border: 1px solid #ccc;
  font-size: 1rem;
  margin-bottom: 20px;
  transition: border-color 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #007bff;
}

/* Results Grid */
.results {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

/* Meal Card Styling */
.meal-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 250px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.meal-card:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

/* Image Styling */
.meal-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

/* Meal Info Styling */
.meal-info {
  padding: 15px;
  text-align: left;
}

.meal-title {
  font-size: 1.2rem;
  color: #007bff;
  margin: 0 0 10px;
}

.meal-instructions {
  font-size: 0.9rem;
  color: #555;
  line-height: 1.4;
}

/* No Results Styling */
.no-results {
  font-size: 1rem;
  color: #888;
  margin-top: 20px;
}
</style>
