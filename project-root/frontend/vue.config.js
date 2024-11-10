const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    host: '::',  // Allow both IPv4 and IPv6 access
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    hot: false,           // Disable Hot Module Replacement
    liveReload: false,    // Disable Live Reload
    client: {
      webSocketURL: 'ws://[2001:7c0:2320:1:f816:3eff:fe09:d4aa]:8083/ws',  // Use IPv6 with brackets
    },
  },
});