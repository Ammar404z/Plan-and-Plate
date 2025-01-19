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
      <button @click="saveRecipe">
        <font-awesome-icon :icon="['fas', 'save']" /> Save
      </button>
      <button @click="toggleShareModal">
        <font-awesome-icon :icon="['fas', 'share-alt']" /> Share
      </button>
      <button @click="goBack">
        <font-awesome-icon :icon="['fas', 'arrow-left']" /> Back to search
      </button>
    </div>
    <!-- Share Modal -->
    <div v-if="isShareModalVisible" class="share-modal">
      <div class="modal-content">
        <h2>Share Recipe</h2>
        <p>Share this recipe with your friends:</p>
        <div class="share-buttons">
          <a
            :href="`https://www.facebook.com/sharer/sharer.php?u=${shareLink}&quote=${encodeURIComponent(
              'Check out this recipe I found:'
            )}`"
            target="_blank"
            class="share-icon"
          >
            <font-awesome-icon :icon="['fab', 'facebook']" />
          </a>
          <a
            :href="`https://wa.me/?text=${encodeURIComponent(
              'Check out this recipe I found: ' + shareLink
            )}`"
            target="_blank"
            class="share-icon"
          >
            <font-awesome-icon :icon="['fab', 'whatsapp']" />
          </a>
          <a
            :href="`https://twitter.com/intent/tweet?url=${shareLink}&text=${encodeURIComponent(
              'Check out this recipe I found:'
            )}`"
            target="_blank"
            class="share-icon"
          >
            <font-awesome-icon :icon="['fab', 'twitter']" />
          </a>
          <button @click="copyLink" class="copy-link-button">
            <font-awesome-icon :icon="['fas', 'copy']" />
          </button>
        </div>
        <button @click="toggleShareModal" class="close-modal">Close</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import api from "@/api";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const mealId = route.params.id;

const isShareModalVisible = ref(false);
const shareLink = `${window.location.origin}/view-meal/${mealId}`;

const meal = ref({
  apiId: "",
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
      apiId: data.idMeal,
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

async function saveRecipe() {
  try {
    // Map the meal data to the recipe payload
    const recipe = {
      apiId: meal.value.apiId, // Ensure the API ID is saved if relevant
      name: meal.value.name,
      ingredients: meal.value.ingredients
        .map((ingredient) => `${ingredient.name} - ${ingredient.measure}`)
        .join(", "),
      instructions: meal.value.instructions || "No instructions available.",
      thumbnail: meal.value.thumbnail,
      category: meal.value.category || "Unknown",
      youTubeVid: meal.value.youTubeVid || "", // Save the link for additional features
    };

    // Send the recipe to the backend
    const response = await api.post("/api/meals/add", recipe);
    alert(`Recipe "${response.data.name}" saved successfully.`);
  } catch (error) {
    console.error("Error saving recipe:", error);
    alert("Failed to save recipe. Please try again.");
  }
}

function toggleShareModal() {
  isShareModalVisible.value = !isShareModalVisible.value;
}

function copyLink() {
  navigator.clipboard
    .writeText(shareLink)
    .then(() => alert("Link copied to clipboard!"))
    .catch(() => alert("Failed to copy the link."));
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
.share-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6); /* Transparent overlay */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.share-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin: 15px 0;
}

.share-icon {
  font-size: 24px;
  color: #555;
  text-decoration: none;
  transition: color 0.3s;
}

.share-icon:hover {
  color: #007bff;
}

.copy-link-button {
  background: none; /* Make the button transparent */
  border: none;
  font-size: 24px;
  color: #555; /* Neutral color for the icon */
  cursor: pointer;
  transition: color 0.3s;
}

.copy-link-button:hover {
  color: #007bff; /* Change to a hover color */
}

.close-modal {
  background: #dc3545;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
}

.close-modal:hover {
  background: #c82333;
}
</style>
