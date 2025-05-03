// Manejadores de eventos al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    // Botón de inicio de sesión
    const loginButton = document.getElementById('login-button');
    // Pantallas e interfaz
    const loginScreen = document.getElementById('login-screen');
    const mainInterface = document.querySelector('.main-interface');

    // Handler para el botón de login
    loginButton.addEventListener('click', () => {
        // Simulación de inicio de sesión
        loginScreen.style.display = 'none';
        mainInterface.classList.remove('hidden');
    });

    // Navegación entre pantallas en la barra lateral
    const menuItems = document.querySelectorAll('.menu-item');
    const screens = document.querySelectorAll('.content-area .screen');

    menuItems.forEach(item => {
        item.addEventListener('click', () => {
            // Eliminar clase activa de todos los elementos de menú
            menuItems.forEach(i => i.classList.remove('active'));
            // Añadir clase activa al elemento seleccionado
            item.classList.add('active');

            // Obtener el id de la pantalla a mostrar
            const screenId = item.getAttribute('data-screen');

            // Ocultar todas las pantallas
            screens.forEach(screen => {
                screen.classList.remove('active');
            });

            // Mostrar la pantalla seleccionada
            document.getElementById(`${screenId}-screen`).classList.add('active');
        });
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

    // Pestañas en la pantalla de base de datos
    const databaseTabs = document.querySelectorAll('.database-tabs .tab');
    const databaseTables = document.querySelectorAll('.database-table-container');

    databaseTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // Eliminar clase activa de todas las pestañas
            databaseTabs.forEach(t => t.classList.remove('active'));
            // Añadir clase activa a la pestaña seleccionada
            tab.classList.add('active');

            // Obtener el id de la tabla a mostrar
            const tableId = tab.getAttribute('data-tab');

            // Ocultar todas las tablas
            databaseTables.forEach(table => {
                table.classList.remove('active');
            });

            // Mostrar la tabla seleccionada
            document.getElementById(tableId).classList.add('active');
        });
    });

    // Gestionar enlaces internos
    const internalLinks = document.querySelectorAll('.view-all-link');

    internalLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            // Obtener el id de la pantalla a mostrar
            const screenId = link.getAttribute('data-screen');

            // Actualizar la navegación
            menuItems.forEach(i => {
                if (i.getAttribute('data-screen') === screenId) {
                    // Simular click en el elemento de menú
                    i.click();
                }
            });
        });
    });

    // Manejo de modales
    const moduleConfigButtons = document.querySelectorAll('.module-config-button:not([disabled])');
    const moduleConfigModal = document.getElementById('module-config-modal');
    const closeModalButton = document.querySelector('.close-modal');
    const cancelButton = document.querySelector('.modal-footer .secondary-button');

    // Abrir modal
    moduleConfigButtons.forEach(button => {
        button.addEventListener('click', () => {
            moduleConfigModal.style.display = 'flex';
        });
    });

    // Cerrar modal
    [closeModalButton, cancelButton].forEach(button => {
        button.addEventListener('click', () => {
            moduleConfigModal.style.display = 'none';
        });
    });

    // Cerrar modal al hacer clic fuera del contenido
    moduleConfigModal.addEventListener('click', (e) => {
        if (e.target === moduleConfigModal) {
            moduleConfigModal.style.display = 'none';
        }
    });
});

// Simular acciones para botones principales
const actionButtons = document.querySelectorAll('.primary-button, .secondary-button, .danger-button');

actionButtons.forEach(button => {
    button.addEventListener('click', (e) => {
        // No hacer nada si está dentro del modal
        if (button.closest('.modal')) {
            return;
        }

        // Mostrar una notificación de simulación
        showNotification('Acción simulada: ' + (button.textContent.trim() || 'Acción ejecutada'), 'info');
    });
});

// Simulación de toggles de módulos
const moduleToggles = document.querySelectorAll('.module-toggle .switch input');

moduleToggles.forEach(toggle => {
    toggle.addEventListener('change', () => {
        const moduleCard = toggle.closest('.module-card');
        if (toggle.checked) {
            moduleCard.classList.add('enabled');
            moduleCard.classList.remove('disabled');
            showNotification('Módulo activado', 'success');
        } else {
            moduleCard.classList.remove('enabled');
            moduleCard.classList.add('disabled');
            showNotification('Módulo desactivado', 'warning');
        }
    });
});

// Simulación de toggles de comandos
const commandToggles = document.querySelectorAll('.command-item .switch input');

commandToggles.forEach(toggle => {
    toggle.addEventListener('change', () => {
        const commandItem = toggle.closest('.command-item');
        const commandName = commandItem.querySelector('.command-name').textContent;

        if (toggle.checked) {
            commandItem.classList.remove('disabled');
            showNotification(`Comando ${commandName} activado`, 'success');
        } else {
            commandItem.classList.add('disabled');
            showNotification(`Comando ${commandName} desactivado`, 'warning');
        }
    });
});

// Botones de visualización/ocultación de contraseñas
const passwordToggleButtons = document.querySelectorAll('.input-with-button .icon-button');

passwordToggleButtons.forEach(button => {
    button.addEventListener('click', () => {
        const input = button.previousElementSibling;
        const icon = button.querySelector('i');

        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    });
});

// Botones de paginación
const paginationButtons = document.querySelectorAll('.pagination-button:not([disabled])');

paginationButtons.forEach(button => {
    button.addEventListener('click', () => {
        showNotification('Acción simulada: Cambio de página', 'info');
    });
});

// Filtrado de logs (simulado)
const logFilterCheckboxes = document.querySelectorAll('.filter-checkbox input');
const logRows = document.querySelectorAll('.log-row');

logFilterCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('change', () => {
        // Obtener todos los filtros activos
        const activeFilters = [];
        logFilterCheckboxes.forEach(cb => {
            if (cb.checked) {
                const filterType = cb.nextElementSibling.classList[1]; // info, warning, error, debug
                activeFilters.push(filterType);
            }
        });

        // Aplicar filtros
        logRows.forEach(row => {
            if (activeFilters.length === 0) {
                row.style.display = 'table-row';
            } else {
                const rowClass = row.classList[1]; // info, warning, error, debug
                row.style.display = activeFilters.includes(rowClass) ? 'table-row' : 'none';
            }
        });
    });
});

// =====================
// Estado del Bot (DASHBOARD)
// =====================
const API_BASE = "http://localhost:8080";

function apiUrl(path) {
    if (path.startsWith("/")) {
      return API_BASE + path;
    }
    return API_BASE + "/" + path;
}

const botStatusFields = {
    estado: document.querySelector('.status-item .status-value'),
    tiempo: document.querySelectorAll('.status-item .status-value')[1],
    version: document.querySelectorAll('.status-item .status-value')[2],
    ram: document.querySelectorAll('.status-item .status-value')[3],
    cpu: document.querySelectorAll('.status-item .status-value')[4],
};
const botControls = document.querySelector('.bot-controls');
if (botControls) {
    // Botones en columna
    botControls.style.flexDirection = 'column';
    botControls.style.gap = '12px';
    // Botones
    const [restartBtn, stopBtn] = botControls.querySelectorAll('button');
    // Crear botón de iniciar si no existe
    let startBtn = botControls.querySelector('.start-button');
    if (!startBtn) {
        startBtn = document.createElement('button');
        startBtn.className = 'primary-button start-button';
        startBtn.textContent = 'Iniciar';
        botControls.insertBefore(startBtn, restartBtn);
    }
    // Función para actualizar estado
    async function updateBotStatus() {
        try {
            const res = await fetch(apiUrl('/api/bot/status'));
            if (!res.ok) {
                throw new Error('No se pudo obtener el estado del bot');
            }
            const data = await res.json();
            botStatusFields.estado.textContent = data.estado;
            botStatusFields.estado.className = 'status-value ' + (data.estado === 'ONLINE' ? 'online' : 'error');
            botStatusFields.tiempo.textContent = data.tiempoActivo;
            botStatusFields.version.textContent = data.version || '-';
            // Si la versión es 1.0.0 pero existe window.BOT_VERSION (inyectada por el backend), usarla
            if (botStatusFields.version.textContent === '1.0.0' && window.BOT_VERSION) {
                botStatusFields.version.textContent = window.BOT_VERSION;
            }
            botStatusFields.ram.textContent = data.ram;
            botStatusFields.cpu.textContent = data.cpu;
            // Botones según estado
            if (data.estado === 'ONLINE') {
                startBtn.disabled = true;
                restartBtn.disabled = false;
                stopBtn.disabled = false;
            } else {
                startBtn.disabled = false;
                restartBtn.disabled = true;
                stopBtn.disabled = true;
            }
        } catch (e) {
            botStatusFields.estado.textContent = 'Desconocido';
            botStatusFields.estado.className = 'status-value error';
            botStatusFields.tiempo.textContent = '-';
            botStatusFields.version.textContent = '-';
            botStatusFields.ram.textContent = '-';
            botStatusFields.cpu.textContent = '-';
            startBtn.disabled = false;
            restartBtn.disabled = true;
            stopBtn.disabled = true;
        }
    }
    // Acciones de los botones
    startBtn.onclick = async (e) => {
        e?.preventDefault?.();
        startBtn.disabled = true;
        showNotification('Iniciando bot...', 'info');
        await fetch(apiUrl('/api/bot/start'), { method: 'POST' });
        setTimeout(updateBotStatus, 1000);
    };
    restartBtn.onclick = async (e) => {
        e?.preventDefault?.();
        restartBtn.disabled = true;
        showNotification('Reiniciando bot...', 'info');
        await fetch(apiUrl('/api/bot/restart'), { method: 'POST' });
        setTimeout(updateBotStatus, 2000);
    };
    stopBtn.onclick = async (e) => {
        e?.preventDefault?.();
        stopBtn.disabled = true;
        showNotification('Deteniendo bot...', 'warning');
        await fetch(apiUrl('/api/bot/stop'), { method: 'POST' });
        setTimeout(updateBotStatus, 1000);
    };
    // Actualización periódica
    setInterval(updateBotStatus, 1000);
    updateBotStatus();
}

// =====================
// Estadísticas de Base de Datos (DASHBOARD y tarjeta)
// =====================
function updateDatabaseStats() {
    fetch(apiUrl('/api/db/stats'))
        .then(res => res.json())
        .then(data => {
            const dbUsuarios = document.querySelector('.database-info .db-item:nth-child(1) .db-count');
            const dbExperiencia = document.querySelector('.database-info .db-item:nth-child(2) .db-count');
            const dbPenalizaciones = document.querySelector('.database-info .db-item:nth-child(3) .db-count');
            const dbStatus = document.querySelector('.database-info .db-item:nth-child(4) .db-count');
            if (dbUsuarios) {
                dbUsuarios.textContent = data.userCount;
            }
            if (dbExperiencia) {
                dbExperiencia.textContent = data.experienceCount;
            }
            if (dbPenalizaciones) {
                dbPenalizaciones.textContent = data.penaltyCount;
            }
            if (dbStatus) {
                dbStatus.textContent = data.available ? 'DISPONIBLE' : 'NO DISPONIBLE';
                dbStatus.classList.remove('online', 'error');
                dbStatus.classList.add(data.available ? 'online' : 'error');
            }
        })
        .catch(() => {
            const dbUsuarios = document.querySelector('.database-info .db-item:nth-child(1) .db-count');
            const dbExperiencia = document.querySelector('.database-info .db-item:nth-child(2) .db-count');
            const dbPenalizaciones = document.querySelector('.database-info .db-item:nth-child(3) .db-count');
            const dbStatus = document.querySelector('.database-info .db-item:nth-child(4) .db-count');
            if (dbUsuarios) {
                dbUsuarios.textContent = '-';
            }
            if (dbExperiencia) {
                dbExperiencia.textContent = '-';
            }
            if (dbPenalizaciones) {
                dbPenalizaciones.textContent = '-';
            }
            if (dbStatus) {
                dbStatus.textContent = 'No disponible';
                dbStatus.classList.remove('disponible');
                dbStatus.classList.add('no-disponible');
            }
        });
}
setInterval(updateDatabaseStats, 2000);
updateDatabaseStats();

// =====================
// Logs recientes (DASHBOARD)
// =====================
const logList = document.getElementById('recent-logs-list');
async function updateRecentLogs() {
    try {
        const res = await fetch(apiUrl('/api/logs?limit=10'));
        if (!res.ok) {
          throw new Error('No se pudieron obtener los logs');
        }
        const logs = await res.json();
        logList.innerHTML = '';
        let count = 0;
        logs.forEach(log => {
            // Nuevo regex para formato: fecha hora NIVEL logger - mensaje
            const match = log.match(/^(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}:\d{2})\s+(INFO|WARN|ERROR)\s+[^-]+-\s+(.*)$/i);
            let time = '', level = '', message = '';
            if (match) {
                time = match[2];
                level = match[3].toLowerCase();
                message = match[4].trim();
            } else {
                // Si no matchea, mostrar todo como info
                level = 'info';
                time = '';
                message = log;
            }
            if (level === 'debug') {
                return;
            } // Excluir DEBUG
            if (count >= 5) {
                return;
            }
            count++;
            const div = document.createElement('div');
            let colorClass = '';
            if (level === 'info') {
                colorClass = 'log-info';
            } else if (level === 'warn' || level === 'warning') {
                colorClass = 'log-warn';
            } else if (level === 'error') {
                colorClass = 'log-error';
            } else {
                colorClass = 'log-other';
            }
            div.className = `log-item ${colorClass}`;
            div.innerHTML = `
                <div class="log-time">${time}</div>
                <span class="log-level-label log-level-${level}">${level.toUpperCase()}</span>
                <div class="log-message">${message}</div>
            `;
            logList.appendChild(div);
        });
        if (count === 0) {
            logList.innerHTML = '<div class="log-item log-info">No hay logs recientes</div>';
        }
    } catch (e) {
        logList.innerHTML = '<div class="log-item log-error">No se pudieron cargar los logs recientes</div>';
    }
}
setInterval(updateRecentLogs, 2000);
updateRecentLogs();

// Añadir estilos para los logs por nivel
(function addLogLevelStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .log-item {
            display: flex;
            align-items: center;
            gap: 10px;
            border-bottom: 1px solid #3332;
            padding: 2px 0 2px 0;
        }
        .log-time {
            min-width: 60px;
            color: #aaa;
            font-size: 0.95em;
        }
        .log-level-label {
            display: inline-block;
            min-width: 48px;
            text-align: center;
            font-weight: bold;
            border-radius: 4px;
            padding: 2px 8px;
            font-size: 0.95em;
            margin-right: 6px;
        }
        .log-level-info {
            background: #e3f1fd;
            color: #2196f3;
        }
        .log-level-warn, .log-level-warning {
            background: #fff4e5;
            color: #ff9800;
        }
        .log-level-error {
            background: #fdeaea;
            color: #f44336;
        }
        .log-level-other {
            background: #ede7f6;
            color: #8e44ad;
        }
        .log-message {
            flex: 1;
            word-break: break-word;
        }
    `;
    document.head.appendChild(style);
})();

// Función para mostrar notificaciones
function showNotification(message, type = 'info') {
    // Crear elemento de notificación
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;

    // Icono según el tipo
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

    // Añadir al DOM
    document.body.appendChild(notification);

    // Animar entrada
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);

    // Eliminar después de un tiempo
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

// Datos simulados para las tablas
const simulatedData = {
    // Añadir más datos si es necesario para futuras funcionalidades
};

// Utilidades
function formatDate(date) {
    return new Date(date).toLocaleDateString('es-ES');
}

function formatDateTime(date) {
    return new Date(date).toLocaleString('es-ES');
}

// Añadir estilos para las notificaciones
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