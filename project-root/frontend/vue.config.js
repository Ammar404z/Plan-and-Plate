const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    host: '::',  // Allows access over both IPv4 and IPv6
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    hot: false,           // Disable Hot Module Replacement
    liveReload: false,    // Disable Live Reload
    client: {
      webSocketURL: 'ws://[2001:7c0:2320:1:f816:3eff:fe09:d4aa]:8081/ws',  // Use brackets for IPv6
    },
  },
});