<template>
  <div class="shopping-list-view">
    <h1>Shopping List</h1>
    <ul>
      <li v-for="(quantity, ingredient) in shoppingList" :key="ingredient">
        {{ ingredient }}: {{ quantity }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import api from "@/api"; // Import your Axios instance
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const shoppingList = ref<Record<string, string | number>>({});
const route = useRoute();

onMounted(async () => {
  try {
    const response = await api.get(`/api/shopping-list/${route.query.planId}`);
    shoppingList.value = response.data; // Assume API sends a flattened shopping list
  } catch (error) {
    console.error("Failed to fetch shopping list:", error);
  }
});
</script>

<style scoped>
.shopping-list-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin: 5px 0;
}
</style>
