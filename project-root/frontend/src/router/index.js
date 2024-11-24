import { createRouter, createWebHistory } from "vue-router";
import RecipeSearch from "../components/RecipeSearch.vue";
import SavedMeals from "../components/SavedMeals.vue";

const routes = [
  { path: "/", name: "RecipeSearch", component: RecipeSearch },
  { path: "/saved-meals", name: "SavedMeals", component: SavedMeals },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
