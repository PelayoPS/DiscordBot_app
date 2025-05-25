const config = {
  appId: 'com.pelayo.discordbotmanager',
  appName: 'Discord Bot Manager',
  webDir: 'www',
  server: {
    androidScheme: 'http',
    allowNavigation: [
      'http://localhost:*',
      'http://127.0.0.1:*',
      'http://192.168.*:*',
      'http://10.*:*',
      'http://172.*:*'
    ],
    cleartext: true
  },
  plugins: {
    SplashScreen: {
      launchShowDuration: 2000,
      launchAutoHide: true,
      backgroundColor: "#2c2f33",
      androidSplashResourceName: "splash",
      androidScaleType: "CENTER_CROP",
      showSpinner: false
    },
    StatusBar: {
      style: 'DARK'
    },
    Keyboard: {
      resize: 'body',
      style: 'dark',
      resizeOnFullScreen: true
    }
  }
};

module.exports = config;
