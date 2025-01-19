<template>
  <div class="view-meal">
    <!-- Meal Header Section -->
    <div class="meal-header">
      <!-- Thumbnail Section -->
      <div class="thumbnail-section">
        <!-- Meal Thumbnail -->
        <img
          :src="meal.thumbnail"
          alt="Meal Thumbnail"
          class="meal-thumbnail"
        />
        <!-- Meal Name and Category -->
        <p class="meal-category">{{ meal.name }}</p>
        <p class="meal-category">Category: {{ meal.category }}</p>
        <!-- Button to watch YouTube video -->
        <button
          v-if="meal.youTubeVid"
          @click="watchYouTubeVid(meal.youTubeVid)"
          class="watch-video-button"
        >
          ► Watch Video
        </button>
      </div>

      <!-- Ingredients Section -->
      <div class="meal-ingredients">
        <h2>Ingredients</h2>
        <ul>
          <!-- Loop through ingredients and display each with its thumbnail -->
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

    <!-- Instructions Section -->
    <div class="meal-instructions">
      <h2>Instructions</h2>
      <p>{{ meal.instructions }}</p>
    </div>

    <!-- Action Buttons -->
    <div class="actions">
      <!-- Button to save the meal -->
      <button @click="saveRecipe">
        <font-awesome-icon :icon="['fas', 'save']" /> Save
      </button>
      <!-- Button to toggle share modal -->
      <button @click="toggleShareModal">
        <font-awesome-icon :icon="['fas', 'share-alt']" /> Share
      </button>
      <!-- Button to go back to the previous page -->
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
          <!-- Share on Facebook -->
          <a
            :href="`https://www.facebook.com/sharer/sharer.php?u=${shareLink}&quote=${encodeURIComponent(
              'Check out this recipe I found:'
            )}`"
            target="_blank"
            class="share-icon"
          >
            <font-awesome-icon :icon="['fab', 'facebook']" />
          </a>
          <!-- Share on WhatsApp -->
          <a
            :href="`https://wa.me/?text=${encodeURIComponent(
              'Check out this recipe I found: ' + shareLink
            )}`"
            target="_blank"
            class="share-icon"
          >
            <font-awesome-icon :icon="['fab', 'whatsapp']" />
          </a>
          <!-- Share on Twitter -->
          <a
            :href="`https://twitter.com/intent/tweet?url=${shareLink}&text=${encodeURIComponent(
              'Check out this recipe I found:'
            )}`"
            target="_blank"
            class="share-icon"
          >
            <font-awesome-icon :icon="['fab', 'twitter']" />
          </a>
          <!-- Copy Link Button -->
          <button @click="copyLink" class="copy-link-button">
            <font-awesome-icon :icon="['fas', 'copy']" />
          </button>
        </div>
        <!-- Close modal button -->
        <button @click="toggleShareModal" class="close-modal">Close</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Import necessary modules and dependencies
import api from "@/api"; // API helper for backend requests
import { onMounted, ref } from "vue"; // Vue composition API for reactivity
import { useRoute, useRouter } from "vue-router"; // Vue router for navigation

// Get route and router instances
const route = useRoute();
const router = useRouter();

// Extract meal ID from route parameters
const mealId = route.params.id;

// Reactive variables
const isShareModalVisible = ref(false); // State for showing/hiding the share modal
const shareLink = `${window.location.origin}/view-meal/${mealId}`; // Shareable link for the meal

// Meal data structure
const meal = ref({
  apiId: "",
  name: "",
  thumbnail: "",
  ingredients: [],
  instructions: "",
  category: "",
  youTubeVid: "",
});

// Fetch meal details on component mount
onMounted(async () => {
  try {
    const response = await api.get(
      `https://www.themealdb.com/api/json/v1/1/lookup.php?i=${mealId}`
    );
    const data = response.data.meals[0];
    // Map API data to local meal structure
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

// Extract ingredients and their thumbnails from the API response
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

// Save the recipe to the backend
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

// Toggle the share modal visibility
function toggleShareModal() {
  isShareModalVisible.value = !isShareModalVisible.value;
}

// Copy the share link to the clipboard
function copyLink() {
  if (navigator.clipboard && navigator.clipboard.writeText) {
    // Modern Clipboard API
    navigator.clipboard
      .writeText(shareLink)
      .then(() => alert("Link copied to clipboard!"))
      .catch(() => alert("Failed to copy the link. Please try manually."));
  } else {
    // Fallback for unsupported browsers
    try {
      const textArea = document.createElement("textarea");
      textArea.value = shareLink;
      document.body.appendChild(textArea);
      textArea.select();
      const successful = document.execCommand("copy");
      document.body.removeChild(textArea);

      if (successful) {
        alert("Link copied to clipboard!");
      } else {
        alert(
          "Failed to copy the link. Please manually copy the link below:\n\n" +
            shareLink
        );
      }
    } catch (error) {
      // Final fallback message
      alert(
        "Your browser does not support copying to the clipboard. Please manually copy the link below:\n\n" +
          shareLink
      );
    }
  }
}

// Navigate back to the previous page
function goBack() {
  router.push("/");
}

// Open the YouTube video in a new tab
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
