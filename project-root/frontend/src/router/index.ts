import { createRouter, createWebHistory } from "vue-router";
import CreateWeeklyPlans from "../views/CreateWeeklyPlans.vue";
import RecipeSearch from "../views/RecipeSearch.vue";
import SavedMeals from "../views/SavedMeals.vue";
import ShoppingListView from "../views/ShoppingListView.vue";
import WeeklyPlansView from "../views/WeeklyPlansView.vue";

const routes = [
  { path: "/", name: "RecipeSearch", component: RecipeSearch },
  { path: "/saved-meals", name: "SavedMeals", component: SavedMeals },
  {
    path: "/create-weekly-plans",
    name: "CreateWeeklyPlan",
    component: CreateWeeklyPlans,
  },
  {
    path: "/view-weekly-plans",
    name: "WeeklyPlans",
    component: WeeklyPlansView,
  },
  { path: "/shopping-list", name: "ShoppingList", component: ShoppingListView },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
