import { createApp } from "vue";
import router from "./router/index"; // Import the router
import App from "./views/App.vue";

createApp(App).use(router).mount("#app");
