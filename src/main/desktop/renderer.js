// renderer.js - Manejo de la barra de ventana personalizada

document.addEventListener('DOMContentLoaded', () => {
  // Crear la barra de ventana personalizada
  createCustomTitleBar()
  
  // Configurar los event listeners para los botones
  setupWindowControls()
})

function createCustomTitleBar() {
  // Crear la barra de título personalizada
  const titleBar = document.createElement('div')
  titleBar.className = 'custom-title-bar'
  titleBar.innerHTML = `
    <div class="title-bar-content">
      <div class="title-bar-drag-region">
        <div class="app-title">
          <i class="fas fa-robot"></i>
          Discord Bot Manager
        </div>
      </div>
      <div class="window-controls">
        <button class="window-control-btn minimize-btn" id="minimize-btn" title="Minimizar">
          <i class="fas fa-window-minimize"></i>
        </button>
        <button class="window-control-btn maximize-btn" id="maximize-btn" title="Maximizar">
          <i class="fas fa-window-maximize"></i>
        </button>
        <button class="window-control-btn close-btn" id="close-btn" title="Cerrar">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>
  `

  // Agregar estilos CSS
  const styles = document.createElement('style')
  styles.textContent = `
    .custom-title-bar {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      height: 40px;
      background: #2c2c2c;
      border-bottom: 1px solid #404040;
      z-index: 10000;
      user-select: none;
      display: flex;
      align-items: center;
    }

    .title-bar-content {
      display: flex;
      align-items: center;
      width: 100%;
      height: 100%;
    }

    .title-bar-drag-region {
      flex: 1;
      height: 100%;
      -webkit-app-region: drag;
      display: flex;
      align-items: center;
      padding-left: 15px;
    }

    .app-title {
      color: #ffffff;
      font-size: 14px;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .app-title i {
      color: #7289da;
    }

    .window-controls {
      display: flex;
      height: 100%;
      -webkit-app-region: no-drag;
    }

    .window-control-btn {
      width: 46px;
      height: 100%;
      border: none;
      background: transparent;
      color: #ffffff;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: background-color 0.2s ease;
      font-size: 12px;
    }

    .window-control-btn:hover {
      background-color: #404040;
    }

    .window-control-btn.close-btn:hover {
      background-color: #e81123;
    }

    .window-control-btn.maximize-btn.is-maximized i:before {
      content: "\\f2d2"; /* fa-window-restore */
    }

    /* Ajustar el contenido principal para que no se superponga con la barra de título */
    body {
      padding-top: 40px !important;
      margin: 0;
    }

    /* Asegurar que el contenido existente no interfiera */
    body > *:not(.custom-title-bar) {
      margin-top: 0 !important;
    }
  `

  // Insertar la barra de título y los estilos al principio del documento
  document.head.appendChild(styles)
  document.body.insertBefore(titleBar, document.body.firstChild)
}

function setupWindowControls() {
  const minimizeBtn = document.getElementById('minimize-btn')
  const maximizeBtn = document.getElementById('maximize-btn')
  const closeBtn = document.getElementById('close-btn')

  // Verificar si electronAPI está disponible
  if (typeof window.electronAPI === 'undefined') {
    console.warn('electronAPI no está disponible. Los controles de ventana no funcionarán.')
    return
  }

  // Event listeners para los botones
  minimizeBtn?.addEventListener('click', () => {
    window.electronAPI.minimizeWindow()
  })

  maximizeBtn?.addEventListener('click', async () => {
    await window.electronAPI.maximizeWindow()
    updateMaximizeButton()
  })

  closeBtn?.addEventListener('click', () => {
    window.electronAPI.closeWindow()
  })

  // Actualizar el icono del botón maximizar según el estado
  async function updateMaximizeButton() {
    const isMaximized = await window.electronAPI.isWindowMaximized()
    const icon = maximizeBtn.querySelector('i')
    if (isMaximized) {
      icon.className = 'fas fa-window-restore'
      maximizeBtn.title = 'Restaurar'
    } else {
      icon.className = 'fas fa-window-maximize'
      maximizeBtn.title = 'Maximizar'
    }
  }

  // Escuchar eventos de maximizado/desmaximizado
  window.electronAPI.onWindowMaximized(() => {
    updateMaximizeButton()
  })

  window.electronAPI.onWindowUnmaximized(() => {
    updateMaximizeButton()
  })

  // Inicializar el estado del botón
  updateMaximizeButton()
}

// Limpiar listeners cuando la página se descarga
window.addEventListener('beforeunload', () => {
  if (window.electronAPI) {
    window.electronAPI.removeAllListeners('window-maximized')
    window.electronAPI.removeAllListeners('window-unmaximized')
  }
})
