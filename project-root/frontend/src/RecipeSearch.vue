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
          <p class="meal-instructions">{{ meal.strInstructions.substring(0, 100) }}...</p>
        </div>
      </div>
    </div>
    
    <!-- Show "No results found" if query is entered and no meals are found -->
    <p v-else-if="query && meals.length === 0" class="no-results">No results found.</p>
  </div>
</template>

<script>
import api from '@/api'; // Import the Axios instance from api.js

export default {
  data() {
    return {
      query: '',
      meals: []
    };
  },
  methods: {
    async searchMeals() {
      if (this.query) {
        try {
          const response = await api.get(`/api/meals/search`, {
            params: { name: this.query }
          });
          this.meals = response.data.meals || [];
        } catch (error) {
          console.error("Error fetching meals:", error);
        }
      } else {
        this.meals = [];
      }
    }
  }
};
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