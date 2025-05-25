const { contextBridge, ipcRenderer } = require('electron')

// Exponer APIs seguras al proceso renderer
contextBridge.exposeInMainWorld('electronAPI', {
  // Funciones para controlar la ventana
  minimizeWindow: () => ipcRenderer.invoke('window-minimize'),
  maximizeWindow: () => ipcRenderer.invoke('window-maximize'),
  closeWindow: () => ipcRenderer.invoke('window-close'),
  isWindowMaximized: () => ipcRenderer.invoke('window-is-maximized'),  // Funciones para manejar la conexión con el backend
  retryBackendConnection: () => ipcRenderer.invoke('retry-backend-connection'),
  checkBackendStatus: () => ipcRenderer.invoke('check-backend-status'),
  launchDiscordBot: () => ipcRenderer.invoke('launch-discord-bot'),

  // Escuchar eventos de la ventana
  onWindowMaximized: (callback) => {
    ipcRenderer.on('window-maximized', callback)
  },
  onWindowUnmaximized: (callback) => {
    ipcRenderer.on('window-unmaximized', callback)
  },

  // Limpiar listeners cuando sea necesario
  removeAllListeners: (channel) => {
    ipcRenderer.removeAllListeners(channel)
  }
})
