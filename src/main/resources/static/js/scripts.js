// scripts.js: Manejadores de eventos globales y utilidades
// Gestiona login, tema global y utilidades de la interfaz

document.addEventListener('DOMContentLoaded', () => {
    // Botón de inicio de sesión
    const loginButton = document.getElementById('login-button');
    const loginScreen = document.getElementById('login-screen');
    const mainInterface = document.querySelector('.main-interface');

    // Handler para el botón de login
    loginButton.addEventListener('click', () => {
        loginScreen.classList.add('hidden');
        loginScreen.classList.remove('active');
        mainInterface.classList.remove('hidden');
        if (window.loadScreen) {
            window.loadScreen('dashboard');
        }
    });

    // Cambio de tema (modo oscuro/claro)
    const themeToggle = document.querySelector('.theme-toggle input');
    const { body } = document;
    themeToggle.addEventListener('change', () => {
        if (themeToggle.checked) {
            body.classList.remove('light-theme');
        } else {
            body.classList.add('light-theme');
        }
    });
});

// Inicialización de pestañas en la pantalla de base de datos


// Muestra notificaciones flotantes
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    let icon = 'info-circle';
    if (type === 'success') {
        icon = 'check-circle';
    }
    if (type === 'warning') {
        icon = 'exclamation-triangle';
    }
    if (type === 'error') {
        icon = 'times-circle';
    }
    notification.innerHTML = `
        <i class="fas fa-${icon}"></i>
        <span>${message}</span>
    `;
    document.body.appendChild(notification);
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

// Utilidades de formato de fecha
function formatDate(date) {
    return new Date(date).toLocaleDateString('es-ES');
}

function formatDateTime(date) {
    return new Date(date).toLocaleString('es-ES');
}

// Estilos para notificaciones
(function addNotificationStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .notification {
            position: fixed;
            bottom: 20px;
            right: 20px;
            background-color: var(--color-surface);
            border-left: 4px solid;
            border-radius: var(--border-radius-md);
            padding: 12px 16px;
            display: flex;
            align-items: center;
            box-shadow: var(--shadow-medium);
            transform: translateX(120%);
            transition: transform 0.3s ease;
            z-index: 1000;
            max-width: 300px;
        }
        .notification.show {
            transform: translateX(0);
        }
        .notification i {
            margin-right: 12px;
            font-size: 18px;
        }
        .notification span {
            flex: 1;
        }
        .notification.info {
            border-color: var(--color-info);
        }
        .notification.info i {
            color: var(--color-info);
        }
        .notification.success {
            border-color: var(--color-success);
        }
        .notification.success i {
            color: var(--color-success);
        }
        .notification.warning {
            border-color: var(--color-warning);
        }
        .notification.warning i {
            color: var(--color-warning);
        }
        .notification.error {
            border-color: var(--color-error);
        }
        .notification.error i {
            color: var(--color-error);
        }
    `;
    document.head.appendChild(style);
})();

// Estilos globales para la dashboard y sección de integraciones
(function addDashboardGlobalStyles() {
    const style = document.createElement('style');
    style.textContent = `
        body, .main-interface {
            font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
            font-size: 15px;
            color: var(--color-text, #e0e0e0);
        }
        .panel, .dashboard-card, .database-info, .integrations-panel {
            background: var(--color-surface, #23272f);
            border-radius: 10px;
            box-shadow: 0 2px 8px #0002;
            padding: 18px 22px 18px 22px;
            margin-bottom: 18px;
        }
        .panel-title, .dashboard-title, .integrations-panel .panel-title {
            font-size: 1.13em;
            font-weight: 600;
            margin-bottom: 12px;
            letter-spacing: 0.01em;
        }
        .status-item, .db-item, .integration-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 7px 0;
            border-bottom: 1px solid #3335;
        }
        .status-item:last-child, .db-item:last-child, .integration-item:last-child {
            border-bottom: none;
        }
        .status-label, .db-label, .integration-label {
            font-weight: 500;
            color: #b0b0b0;
            font-size: 0.98em;
        }
        .status-value, .db-count, .integration-value {
            font-weight: 600;
            font-size: 1.04em;
        }
        .status-value.online, .db-count.online {
            color: #4caf50;
        }
        .status-value.error, .db-count.error {
            color: #f44336;
        }
        .integrations-panel {
            min-height: 180px;
        }
        .integration-value {
            color: #e0e0e0;
        }
        .integration-value.empty {
            color: #888;
            font-style: italic;
        }
        .bot-controls {
            margin-top: 18px;
        }
        .dashboard-card {
            border: none;
        }
        .logs-panel {
            background: var(--color-surface, #23272f);
            border-radius: 10px;
            box-shadow: 0 2px 8px #0002;
            padding: 18px 22px;
        }
    `;
    document.head.appendChild(style);
})();