import { createApp } from "vue";
import router from "./router/index"; // Import the router
import App from "./views/App.vue";

import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

// Import regular and solid stars
import { faStar as farStar } from "@fortawesome/free-regular-svg-icons";
import { faStar as fasStar } from "@fortawesome/free-solid-svg-icons";

// Add the icons to the library
library.add(farStar, fasStar);

// Create the app instance
const app = createApp(App);

// Register the Font Awesome component globally
app.component("font-awesome-icon", FontAwesomeIcon);

// Use the router and mount the app
app.use(router).mount("#app");
