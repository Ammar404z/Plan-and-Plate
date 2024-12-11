import { createRouter, createWebHistory } from "vue-router";
import RecipeSearch from "../views/RecipeSearch.vue";
import SavedMeals from "../views/SavedMeals.vue";

const routes = [
  { path: "/", name: "RecipeSearch", component: RecipeSearch },
  { path: "/saved-meals", name: "SavedMeals", component: SavedMeals },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
