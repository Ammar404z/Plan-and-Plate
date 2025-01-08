<template>
  <div class="add-meal">
    <h1>Add Custom Meal</h1>
    <form @submit.prevent="addMeal">
      <div>
        <label for="name">Meal Name:</label>
        <input id="name" v-model="meal.name" required />
      </div>
      <div>
        <label>Ingredients:</label>
        <div
          v-for="(ingredient, index) in meal.ingredients"
          :key="index"
          class="ingredient-input"
        >
          <input
            v-model="ingredient.name"
            placeholder="Ingredient Name"
            required
          />
          <input
            v-model="ingredient.quantity"
            placeholder="Quantity"
            required
          />
          <input
            v-model="ingredient.unit"
            placeholder="Unit (e.g., cups, tsp)"
          />
          <button type="button" @click="removeIngredient(index)">Remove</button>
        </div>
        <button type="button" @click="addIngredient">Add Ingredient</button>
      </div>
      <div>
        <label for="instructions">Instructions:</label>
        <textarea
          id="instructions"
          v-model="meal.instructions"
          required
        ></textarea>
      </div>
      <div>
        <label for="thumbnail">Thumbnail URL (Optional):</label>
        <input id="thumbnail" v-model="meal.thumbnail" />
      </div>
      <button type="submit">Add Meal</button>
    </form>
  </div>
</template>

<script setup>
import api from "@/api";
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const meal = ref({
  name: "",
  ingredients: [],
  instructions: "",
  thumbnail: "",
});

function addIngredient() {
  meal.value.ingredients.push({ name: "", quantity: "", unit: "" });
}

function removeIngredient(index) {
  meal.value.ingredients.splice(index, 1);
}

async function addMeal() {
  try {
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
/* Add styling for ingredient inputs */
.ingredient-input {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
</style>
