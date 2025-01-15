<template>
  <div class="search-container">
    <!-- Title with a ref -->
    <h1 ref="titleRef">Find Recipes For Your Meals</h1>

    <!-- Filters with a ref for the dropdown -->
    <div class="filters">
      <select ref="dropdownRef" v-model="selectedFilter" @change="applyFilters">
        <option value="">All Filters</option>
        <option
            v-for="filter in combinedFilters"
            :key="filter.type + filter.value"
            :value="filter.value"
        >
          {{ filter.type }}: {{ filter.value }}
        </option>
      </select>
    </div>

    <!-- Search bar -->
    <input
        v-model="query"
        placeholder="Search for a recipe"
        @input="searchMeals"
        class="search-input"
    />

    <!-- Results -->
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

/* -----------------------------
   TypeScript Interfaces
----------------------------- */
//Define the structure of a Meal from TheMealDB API
interface Meal {
  id: string;
  name: string;
  ingredients: string;
  instructions: string;
  thumbnail: string;
  category: string;
  [key: string]: any;
}

/* -----------------------------
   Refs & Reactive Data
----------------------------- */
const titleRef = ref<HTMLElement | null>(null);
const dropdownRef = ref<HTMLElement | null>(null);

const query = ref("");
const meals = ref<Meal[]>([]);


//for filtering purposes
const combinedFilters = ref<{ type: string; value: string }[]>([]);
const selectedFilter = ref<string>("");

/* -----------------------------
   Lifecycle Hook
----------------------------- */
onMounted(() => {
  fetchFilters();

  // Match dropdown width to title
  if (titleRef.value && dropdownRef.value) {
    const titleWidth = titleRef.value.offsetWidth;
    dropdownRef.value.style.width = `${titleWidth}px`;
  }
});

/* -----------------------------
   Functions
----------------------------- */

// fetch the filters to be displayed in the dropdown menu
async function fetchFilters() {
  try {
    // Fetch combined categories and areas from the backend
    const response = await api.get("/api/meals/categoriesAndAreas");

    // Populate the combinedFilters array
    combinedFilters.value = response.data.filters.map((filter: any) => ({
      type: filter.type,
      value: filter.value,
    }));
  } catch (error) {
    console.error("Error fetching combined filters:", error);
  }
}

// Fetch all saved meals on component mount
async function fetchAllMeals() {
  try {
    const response = await api.get("/api/meals");
    meals.value = response.data.map((meal: any) => ({
      id: meal.id,
      name: meal.name,
      thumbnail: meal.thumbnail,
      instructions: meal.instructions || "No instructions available.",
      ingredients: meal.ingredients,
      category: meal.category || "Unknown",
    }));
  } catch (error) {
    console.error("Error fetching all meals:", error);
  }
}

async function applyFilters() {
  try {
    // If no filter is selected (All Filters), fetch all meals:
    if (!selectedFilter.value) {
      await fetchAllMeals();
      return; // Stop here, since we don't need to call the filter endpoint
    }

    // Otherwise, proceed with normal filtering:
    let url = "/api/meals/filter";
    const params = new URLSearchParams();

    // Determine if the selected filter is a category or area
    const selected = combinedFilters.value.find(
        (filter) => filter.value === selectedFilter.value
    );

    if (selected) {
      if (selected.type === "Category") {
        params.append("category", selected.value);
      } else if (selected.type === "Area") {
        params.append("area", selected.value);
      }
    }

    url += `?${params.toString()}`;

    // Fetch filtered meals
    const response = await api.get(url);
    meals.value = (response.data.meals || []).map((meal: any) => ({
      id: meal.idMeal,
      name: meal.strMeal,
      thumbnail: meal.strMealThumb,
    }));
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
        id: meal.idMeal,
        name: meal.strMeal,
        thumbnail: meal.strMealThumb,
        instructions: meal.strInstructions || "No instructions available.",
        ingredients: extractIngredients(meal),
        category: meal.strCategory || "Unknown",
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
    const ingredient = meal[`strIngredient${i}`];
    const measure = meal[`strMeasure${i}`];

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
    // Fetch full meal details by id from the backend or API(we have to do that because
    // the filtered meals for example don't contain the full meal details)

    const response = await api.get(
        `https://www.themealdb.com/api/json/v1/1/lookup.php?i=${meal.id}`
    );
    const fullMeal = response.data.meals[0];

    // Map the full meal details to the recipe payload
    const recipe = {
      name: fullMeal.strMeal || "Unnamed Recipe",
      ingredients: extractIngredients(fullMeal),
      instructions: fullMeal.strInstructions || "No instructions available.",
      thumbnail: fullMeal.strMealThumb,
      category: fullMeal.strCategory || "Unknown",
    };

    // Send the full meal details to the backend
    const saveResponse = await api.post("/api/meals/add", recipe);
    alert(`Recipe "${saveResponse.data.name}" saved successfully.`);
  } catch (error) {
    console.error("Error saving recipe:", error);
    alert("Failed to save recipe. Please try again.");
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

/* Heading (title) Styling */
h1 {
  font-size: 2.2rem; /* Slightly larger for emphasis */
  margin-bottom: 10px; /* Reduce space below the title */
  margin-top: 20px; /* Add space above the title */
  color: #333;
  text-align: center;

  /* Box styling */
  padding: 15px 25px;
  border: 2px solid #89cff0;
  border-radius: 10px;
  background-color: #f0f8ff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  display: inline-block;
}

/* Filters section */
.filters {
  margin-bottom: 10px;
  text-align: center;
}

/* Dropdown styling (no fixed width; now set by JavaScript) */
.filters select {
  font-size: 1rem;
  padding: 10px;
  border: 2px solid #89cff0;
  border-radius: 10px;
  background-color: #f0f8ff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin: 0 auto;
  display: block;
}

/* Search Input */
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
  border-color: #89cff0;
}

/* Results Grid */
.results {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

/* Meal Card */
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


/* Save Recipe Button Styling */
.meal-info button {
  background-color: #007bff; /* Blue background */
  color: white; /* White text */
  border: none;
  padding: 10px 15px;
  font-size: 1rem; /* Increase font size */
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif; /* Elegant font */
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.meal-info button:hover {
  background-color: #0056b3; /* Darker blue on hover */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* Shadow effect */
  transform: translateY(-2px); /* Lift effect */
}


/* Meal Image */
.meal-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

/* Meal Info */
.meal-info {
  padding: 15px;
  text-align: left;
}
.meal-title {
  font-size: 1.2rem;
  color: #61c8ca;
  margin: 0 0 10px;
}
.meal-instructions {
  font-size: 0.9rem;
  color: #555;
  line-height: 1.4;
}

/* No Results */
.no-results {
  font-size: 1rem;
  color: #888;
  margin-top: 20px;
}
</style>
