<template>
  <div class="search-container">
    <h1>Recipe Search</h1>
    <!-- Dropdown for categories -->
    <div class="filters">
      <select v-model="selectedCategory" @change="applyFilters">
        <option value="">All Categories</option>
        <option
          v-for="category in categories"
          :key="category"
          :value="category"
        >
          {{ category }}
        </option>
      </select>

      <!-- Dropdown for areas -->
      <select v-model="selectedArea" @change="applyFilters">
        <option value="">All Areas</option>
        <option v-for="area in areas" :key="area" :value="area">
          {{ area }}
        </option>
      </select>
    </div>

    <!--search bar-->
    <input
      v-model="query"
      placeholder="Search for a recipe"
      @input="searchMeals"
      class="search-input"
    />

    <!-- Wrapper element with v-if applied -->
    <div v-if="meals.length > 0" class="results">
      <div class="meal-card" v-for="meal in meals" :key="meal.id">
        <img :src="meal.thumbnail" alt="Meal Image" class="meal-image" />
        <div class="meal-info">
          <h3 class="meal-title">{{ meal.name }}</h3>

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
import { onMounted, ref } from "vue";

//Define the structure of a Meal from TheMealDB API
interface Meal {
  id: string;
  name: string;
  ingredients: string;
  instructions: string;
  thumbnail: string;
  [key: string]: any; // we do this to allow dynamic ingredient keys like strIngredient1, strMeasure1, etc...
}

const query = ref("");
const meals = ref<Meal[]>([]);

//for filtering purposes
const categories = ref<string[]>([]);
const areas = ref<string[]>([]);
const selectedCategory = ref<string>("");
const selectedArea = ref<string>("");

// fetch the filters to be displayed in the dropdown menu
async function fetchFilters() {
  try {
    // Fetch categories from backend
    const categoryResponse = await api.get("/api/meals/categories");
    console.log("Category Response:", categoryResponse.data);
    categories.value = categoryResponse.data.meals.map(
      (category: any) => category.strCategory
    );

    // Fetch areas from backend
    const areaResponse = await api.get("/api/meals/areas");
    console.log("Area Response:", areaResponse.data);
    areas.value = areaResponse.data.meals.map((area: any) => area.strArea);

    console.log("Categories:", categories.value);
    console.log("Areas:", areas.value);
  } catch (error) {
    console.error("Error fetching filters:", error);
  }
}

async function applyFilters() {
  try {
    // Ensure at least one filter is selected
    if (!selectedCategory.value && !selectedArea.value) {
      console.error("No filters selected. Skipping API call.");
      return;
    }

    let url = "/api/meals/filter";

    // Add query parameters based on filters
    if (selectedCategory.value) {
      url += `?category=${encodeURIComponent(selectedCategory.value)}`;
    } else if (selectedArea.value) {
      url += `?area=${encodeURIComponent(selectedArea.value)}`;
    }

    // Fetch filtered meals
    const response = await api.get(url);
    meals.value = (response.data.meals || []).map((meal: any) => ({
      id: meal.idMeal,
      name: meal.strMeal,
      thumbnail: meal.strMealThumb,
    }));

    console.log("Filtered meals:", meals.value);
  } catch (error) {
    console.error("Error applying filters:", error);
  }
}

async function searchMeals() {
  if (query.value) {
    try {
      const response = await api.get("/api/meals/search", {
        params: { name: query.value },
      });

      // Map API response to your custom field names (Issue #3)
      meals.value = (response.data.meals || []).map((meal: any) => ({
        id: meal.idMeal, // Map idMeal to id
        name: meal.strMeal, // Map strMeal to name
        thumbnail: meal.strMealThumb, // Map strMealThumb to thumbnail
        instructions: meal.strInstructions || "No instructions available.", // Map strInstructions to instructions
        ingredients: extractIngredients(meal), // Dynamically extract ingredients
      }));
    } catch (error) {
      console.error("Error fetching meals:", error);
    }
  } else {
    meals.value = [];
  }
}

function extractIngredients(meal: any): string {
  const ingredients: string[] = [];
  for (let i = 1; i <= 20; i++) {
    const ingredient = meal[`strIngredient${i}`]; //
    const measure = meal[`strMeasure${i}`]; // we have to use strMeasure here since its what we are getting back from the api, same goes for strIngrediet

    if (ingredient && ingredient.trim() !== "") {
      ingredients.push(
        `${ingredient.trim()} - ${measure?.trim() || "as needed"}`
      );
    }
  }

  return ingredients.length > 0
    ? ingredients.join(", ")
    : "No ingredients available.";
}

async function saveRecipe(meal: Meal) {
  try {
    // Fetch full meal details by id from the backend or API
    const response = await api.get(
      `https://www.themealdb.com/api/json/v1/1/lookup.php?i=${meal.id}`
    );
    const fullMeal = response.data.meals[0]; // Assuming the API returns an array with one meal object

    // Map the full meal details to the recipe payload
    const recipe = {
      name: fullMeal.strMeal || "Unnamed Recipe",
      ingredients: extractIngredients(fullMeal), // Extract ingredients dynamically
      instructions: fullMeal.strInstructions || "No instructions available.",
      thumbnail: fullMeal.strMealThumb,
    };

    console.log("Payload being sent:", recipe); // Debug log

    // Send the full meal details to the backend
    const saveResponse = await api.post("/api/meals/add", recipe);
    alert(`Recipe "${saveResponse.data.name}" saved successfully.`);
  } catch (error) {
    console.error("Error saving recipe:", error);
    alert("Failed to save recipe. Please try again.");
  }
}

onMounted(() => {
  fetchFilters();
});
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
