const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    host: "::", // Allows access over both IPv4 and IPv6
    headers: {
      "Access-Control-Allow-Origin": "*",
    },
    hot: false, // Disable Hot Module Replacement
    liveReload: false, // Disable Live Reload
  },
});
