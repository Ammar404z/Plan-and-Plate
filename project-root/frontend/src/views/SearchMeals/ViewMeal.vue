<template>
  <div class="view-meal">
    <div class="meal-header">
      <!-- Thumbnail -->
      <div class="thumbnail-section">
        <img
          :src="meal.thumbnail"
          alt="Meal Thumbnail"
          class="meal-thumbnail"
        />
        <p class="meal-category">{{ meal.name }}</p>
        <p class="meal-category">Category: {{ meal.category }}</p>
        <button
          v-if="meal.youTubeVid"
          @click="watchYouTubeVid(meal.youTubeVid)"
          class="watch-video-button"
        >
          ► Watch Video
        </button>
      </div>

      <!-- Ingredients -->
      <div class="meal-ingredients">
        <h2>Ingredients</h2>
        <ul>
          <li
            v-for="ingredient in meal.ingredients"
            :key="ingredient.name"
            class="ingredient-item"
          >
            <img
              :src="ingredient.thumbnailSmall"
              :alt="ingredient.name"
              class="ingredient-thumbnail"
            />
            {{ ingredient.name }} - {{ ingredient.measure }}
          </li>
        </ul>
      </div>
    </div>

    <!-- Instructions -->
    <div class="meal-instructions">
      <h2>Instructions</h2>
      <p>{{ meal.instructions }}</p>
    </div>

    <!-- Action Buttons -->
    <div class="actions">
      <button @click="saveRecipe">Save Recipe</button>
      <button @click="shareMeal">Share Recipe</button>
      <button @click="goBack">Back to Search</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/api";

const route = useRoute();
const router = useRouter();
const mealId = route.params.id;

const meal = ref({
  name: "",
  thumbnail: "",
  ingredients: [],
  instructions: "",
  category: "",
  youTubeVid: "",
});

onMounted(async () => {
  try {
    const response = await api.get(
      `https://www.themealdb.com/api/json/v1/1/lookup.php?i=${mealId}`
    );
    const data = response.data.meals[0];
    meal.value = {
      name: data.strMeal,
      thumbnail: data.strMealThumb,
      ingredients: extractIngredients(data),
      instructions: data.strInstructions || "No instructions available.",
      category: data.strCategory || "Unknown",
      youTubeVid: data.strYoutube,
    };
  } catch (error) {
    console.error("Error fetching meal details:", error);
  }
});

function extractIngredients(data) {
  const ingredients = [];
  for (let i = 1; i <= 20; i++) {
    const ingredient = data[`strIngredient${i}`];
    const measure = data[`strMeasure${i}`];

    if (ingredient && ingredient.trim()) {
      ingredients.push({
        name: ingredient.trim(),
        measure: measure?.trim() || "as needed",
        thumbnailSmall: `https://www.themealdb.com/images/ingredients/${ingredient.trim()}-Small.png`,
        thumbnail: `https://www.themealdb.com/images/ingredients/${ingredient.trim()}.png`,
      });
    }
  }
  return ingredients;
}

function saveRecipe() {
  alert(`Saving recipe: ${meal.value.name}`);
}

function shareMeal() {
  const shareLink = `${window.location.origin}/view-meal/${mealId}`;
  navigator.clipboard.writeText(shareLink).then(() => {
    alert("Link copied to clipboard!");
  });
}

function goBack() {
  router.push("/");
}
async function watchYouTubeVid(youTubeVid: string) {
  window.open(youTubeVid, "_blank");
}
</script>

<style scoped>
.view-meal {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

/* Header Section */
.meal-header {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  align-items: flex-start;
}

/* Thumbnail Section */
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

.watch-video-button {
  padding: 10px 20px;
  font-size: 1rem;
  background-color: #dc3545; /* Red */
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.watch-video-button:hover {
  background-color: #c82333;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

/* Ingredients Section */
.meal-ingredients {
  flex: 1;
}

.meal-ingredients h2 {
  margin: 0 0 10px;
  color: #333;
}

.ingredient-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 1rem;
  color: #555;
}

.ingredient-thumbnail {
  width: 50px; /* Increased size */
  height: 50px;
  object-fit: cover;
  margin-right: 10px;
  border-radius: 5px;
  border: 1px solid #ddd;
}

/* Instructions Section */
.meal-instructions {
  margin-top: 20px;
  font-size: 1rem;
  line-height: 1.5;
  text-align: justify;
  color: #444;
  background-color: #f7faff;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e3f2fd;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.meal-instructions h2 {
  margin-bottom: 10px;
  color: #333;
}

/* Actions Section */
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
  transition: all 0.3s ease;
}

.actions button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

/* Save Button */
.actions button:nth-child(1) {
  background-color: #007bff;
  color: white;
}

.actions button:nth-child(1):hover {
  background-color: #0056b3;
}

/* Share Button */
.actions button:nth-child(2) {
  background-color: #28a745;
  color: white;
}

.actions button:nth-child(2):hover {
  background-color: #218838;
}

/* Back Button */
.actions button:nth-child(3) {
  background-color: #ffc107;
  color: white;
}

.actions button:nth-child(3):hover {
  background-color: #e0a800;
}
</style>
