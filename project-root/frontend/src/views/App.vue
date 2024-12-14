<template>
  <div id="app">
    <nav>
      <router-link to="/">Search Recipes</router-link>
      <router-link to="/saved-meals">Saved Meals</router-link>
      <router-link to="/view-weekly-plans">Weekly Plans</router-link>
      <router-link to="/statistics">Statistics</router-link>
    </nav>
    <router-view />
  </div>
</template>

<script>
export default {
  name: "App",
};
</script>

<style>
/* General Styling for Navigation Bar */
nav {
  background-color: #b98f42;
  padding: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem; /* Add spacing between the links */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Styling for Router Links */
router-link {
  margin: 0;
  text-decoration: none;
  color: white; /* Change text color to white */
  font-size: 1.1rem;
  font-weight: 500;
  transition: color 0.3s ease, background-color 0.3s ease;
  padding: 0.5rem 1rem;
  border-radius: 5px;
}

/* Hover Effect for Links */
router-link:hover {
  color: #76a08d; /* Light green color on hover */
  background-color: white; /* Add a background color on hover */
  text-decoration: none;
}

/* Active Link Styling */
router-link-active {
  font-weight: bold;
  color: #ffffff; /* Keep active link white */
  border-bottom: 2px solid white; /* Subtle underline for active link */
}

/* Styling for App Wrapper */
#app {
  font-family: Arial, sans-serif;
  color: #333;
  padding: 0;
  margin: 0;
  min-height: 100vh;
  background-color: #f9f9f9;
}
</style>
