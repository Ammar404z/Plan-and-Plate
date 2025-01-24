<template>
  <div class="add-meal">
    <h1>Add Custom Meal</h1>
    <!-- Form to add a custom meal -->
    <form @submit.prevent="addMeal">
      <div>
        <label for="name">Meal Name:</label>
        <!-- Input for meal name -->
        <input id="name" v-model="meal.name" required />
      </div>

      <div>
        <label>Ingredients:</label>
        <!-- Loop through ingredients to dynamically render input rows -->
        <div
          v-for="(ingredient, index) in ingredients"
          :key="index"
          class="ingredient-row"
        >
          <!-- Input for ingredient name -->
          <input
            v-model="ingredient.name"
            placeholder="Ingredient (e.g., Rice)"
            required
          />
          <!-- Input for ingredient quantity -->
          <input
            v-model="ingredient.quantity"
            type="number"
            min="0.1"
            step="0.1"
            placeholder="Quantity (e.g., 1)"
            required
          />
          <!-- Input for ingredient unit -->
          <input
            v-model="ingredient.unit"
            placeholder="Unit (e.g., cup)"
            required
          />
          <!-- Button to remove the ingredient row -->
          <button type="button" @click="removeIngredient(index)">Remove</button>
        </div>
        <!-- Button to add a new ingredient row -->
        <button type="button" @click="addIngredient">Add Ingredient</button>
      </div>

      <div>
        <label for="instructions">Instructions:</label>
        <!-- Textarea for meal instructions -->
        <textarea
          id="instructions"
          v-model="meal.instructions"
          required
        ></textarea>
      </div>
      <div>
        <label for="thumbnail">Thumbnail URL (Optional):</label>
        <!-- Input for meal thumbnail URL -->
        <input id="thumbnail" v-model="meal.thumbnail" />
      </div>
      <div>
        <label for="category">Category:</label>
        <!-- Dropdown for selecting category -->
        <select id="category" v-model="meal.category" required>
          <option value="" disabled selected>Select a category</option>
          <option value="Beef">Category: Beef</option>
          <option value="Breakfast">Category: Breakfast</option>
          <option value="Chicken">Category: Chicken</option>
          <option value="Dessert">Category: Dessert</option>
          <option value="Goat">Category: Goat</option>
          <option value="Lamb">Category: Lamb</option>
          <option value="Miscellaneous">Category: Miscellaneous</option>
          <option value="Pasta">Category: Pasta</option>
          <option value="Pork">Category: Pork</option>
          <option value="Seafood">Category: Seafood</option>
          <option value="Side">Category: Side</option>
          <option value="Starter">Category: Starter</option>
          <option value="Vegan">Category: Vegan</option>
          <option value="Vegetarian">Category: Vegetarian</option>
        </select>
      </div>
      <!-- Submit button to save the meal -->
      <button type="submit">Add Meal</button>
      <!-- Cancel button to navigate back to saved meals -->
      <button @click="router.push(`/saved-meals`)" type="submit">Cancel</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import api from "@/api"; // API module for backend requests
import { ref } from "vue"; // Vue's reactive state management
import { useRouter } from "vue-router"; // Vue Router for navigation

const router = useRouter(); // Router instance for navigation
// Reactive state for meal details
const meal = ref({
  name: "",
  ingredients: "",
  instructions: "",
  thumbnail: "",
  category: "",
  isCustom: true,
});

// Array to hold individual ingredient components
const ingredients = ref([
  { name: "", quantity: "", unit: "" }, // Initial ingredient row
]);

// Add a new empty ingredient row
function addIngredient() {
  ingredients.value.push({ name: "", quantity: "", unit: "" });
}

// Remove an ingredient row by index
function removeIngredient(index: number) {
  ingredients.value.splice(index, 1);
}

// Combine the ingredients into the desired format for backend submission
function combineIngredients(): string {
  return ingredients.value
    .map(
      (ingredient) =>
        `${ingredient.name} - ${ingredient.quantity} ${ingredient.unit}`
    )
    .join(", ");
}

// Validate the thumbnail URL to ensure it's a valid HTTPS link
function validateThumbnailUrl(url: string): boolean {
  return url.startsWith("https://");
}

// Submit the meal form
async function addMeal() {
  try {
    // Combine ingredients into the expected string format
    meal.value.ingredients = combineIngredients();

    // Ensure all fields are properly filled
    if (!meal.value.ingredients) {
      alert("Please add at least one ingredient.");
      return;
    }
    if (!meal.value.category) {
      alert("Please select a category.");
      return;
    }

    if (meal.value.thumbnail && !validateThumbnailUrl(meal.value.thumbnail)) {
      alert("Invalid thumbnail URL. Please enter a valid URL.");
      return;
    }

    meal.value.isCustom = true;

    // Save the meal to the backend
    await api.post("/api/meals/add-custom", meal.value);
    alert("Meal added successfully!");
    router.push("/saved-meals");
  } catch (error) {
    console.error("Error adding meal:", error);
    alert("Failed to add meal.");
  }
}
</script>

<style scoped>
/* Center the form and make it responsive */
.add-meal {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Headline styling */
h1 {
  text-align: center;
  color: #333;
  font-size: 24px;
  margin-bottom: 20px;
}

/* Form styling */
form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

label {
  font-size: 16px;
  font-weight: bold;
  color: #555;
}

/* Input and textarea styling */
input,
textarea {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
  transition: border-color 0.2s;
}

input:focus,
textarea:focus {
  border-color: #007bff;
  outline: none;
}

/* Ingredient row styling */
.ingredient-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.ingredient-row input {
  flex: 1;
}

/* Remove button styling for ingredient rows */
button[type="button"] {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 5px 10px;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

button[type="button"]:hover {
  background-color: #bd2130;
}

/* Add Ingredient button styling */
.add-meal button[type="button"] {
  background-color: #28a745;
  color: white;
  font-size: 14px;
  padding: 8px 15px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
}

.add-meal button[type="button"]:hover {
  background-color: #218838;
}

/* Submit button styling */
button[type="submit"] {
  background-color: #007bff;
  color: white;
  font-size: 16px;
  font-weight: bold;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

button[type="submit"]:hover {
  background-color: #0056b3;
}

/* Add spacing between rows */
.ingredient-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}
select {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
  transition: border-color 0.2s;
}

select:focus {
  border-color: #007bff;
  outline: none;
}
</style>
