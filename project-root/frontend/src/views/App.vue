<template>
  <div id="app">
    <nav>
      <router-link to="/">Search Recipes</router-link>
      <router-link to="/saved-meals"> Saved Meals</router-link>
      <router-link to="/view-weekly-plans"> Weekly-plans </router-link>
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
nav {
  background-color: #f8f9fa;
  padding: 1em;
}
router-link {
  margin-right: 1em;
  text-decoration: none;
  color: #42b983;
}
router-link:hover {
  color: #66d9ef;
}
</style>
