const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8083,
    host: '0.0.0.0',
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    hot: false,           // Disable Hot Module Replacement
    liveReload: false,    // Disable Live Reload
    client: {
      webSocketURL: 'ws://193.196.52.222:8083/ws',
    },
  },
});