import AddMeal from "@/views/SavedMeals/AddMeal.vue";
import StatisticsView from "@/views/Statistics/StatisticsView.vue";
import { createRouter, createWebHistory } from "vue-router";
import SavedMeals from "../views/SavedMeals/SavedMeals.vue";
import RecipeSearch from "../views/SearchMeals/RecipeSearch.vue";
import CreateWeeklyPlans from "../views/WeeklyPlans/CreateWeeklyPlans.vue";
import ShoppingListView from "../views/WeeklyPlans/ShoppingListView.vue";
import WeeklyPlansView from "../views/WeeklyPlans/WeeklyPlansView.vue";

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
  { path: "/statistics", name: "Statisticsview", component: StatisticsView },
  { path: "/add-meal", name: "AddMeal", component: AddMeal },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
