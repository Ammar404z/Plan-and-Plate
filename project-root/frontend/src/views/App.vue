<template>
  <div id="app">
    <header class="header">
      <img src="@/assets/supermarket.png" alt="Supermarket Logo" class="logo" />
      <h1 class="app-title">Plan & Plate</h1>
    </header>

    <!-- Add a gradient background for the nav section -->
    <div class="nav-background">
      <nav class="nav-bar">
        <router-link to="/" class="nav-button">
          <img
            src="@/assets/pngegg (2).png"
            alt="Search Recipes"
            class="nav-icon"
          />
          <span>Search Recipes</span>
        </router-link>
        <router-link to="/saved-meals" class="nav-button">
          <img src="@/assets/food.png" alt="Saved Meals" class="nav-icon" />
          <span>Saved Meals</span>
        </router-link>
        <router-link to="/view-weekly-plans" class="nav-button">
          <img
            src="@/assets/nutrition-plan.png"
            alt="Weekly Plans"
            class="nav-icon"
          />
          <span>Weekly Plans</span>
        </router-link>
        <router-link to="/statistics" class="nav-button">
          <img src="@/assets/portion.png" alt="Statistics" class="nav-icon" />
          <span>Statistics</span>
        </router-link>
      </nav>
    </div>

    <router-view />
  </div>
</template>

<script>
export default {
  name: "App",
};
</script>

<style>
/* HEADER */
.header {
  background-color: #89cff0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1.5rem;
  height: 100px;
  box-sizing: border-box;
}

.logo {
  height: 60px; /* Make the logo slightly larger */
  width: auto; /* Maintain aspect ratio */
  margin-right: 1rem;
}

.app-title {
  font-size: 2.2rem;
  font-weight: 700;
}

/* General Styling for Navigation Bar */
/* NAV BACKGROUND */
.nav-background {
  background: linear-gradient(
    to bottom,
    #89cff0 50%,
    #ffffff 50%
  ); /* Top: Light blue, Bottom: White */
  padding: 2rem 0;
}

/* NAVIGATION BAR */
.nav-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 2rem; /* Space between navigation items */
}

/* STYLING FOR NAVIGATION BUTTONS */
.nav-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none; /* Remove underlines */
  background-color: #f9f9f9;
  padding: 1rem;
  border-radius: 10px;
  width: 120px;
  height: 120px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  font-size: 1rem;
  color: #333; /* Default text color */
  text-align: center;
}

.nav-button:hover {
  transform: translateY(-5px); /* Lift effect */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  background-color: #ffe5e7; /* Subtle hover color */
}

.nav-button span {
  margin-top: 0.5rem; /* Space between icon and title */
  font-size: 1rem;
  font-weight: bold;
}

/* ICON STYLING */
.nav-icon {
  width: 40px;
  height: 40px;
}

/* MAIN CONTENT */
#app {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  color: #333;
}
</style>
