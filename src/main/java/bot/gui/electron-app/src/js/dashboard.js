// dashboard.js: Lógica JS específica para el dashboard
// Aquí irá la lógica JS del dashboard extraída de main.js

// Lógica del dashboard: estado del bot y logs recientes
// =====================
// Encapsular la lógica de inicialización del dashboard en una función reutilizable
function initDashboardBotControls() {
    const botStatusFields = {
        estado: document.querySelector('.status-item .status-value'),
        tiempo: document.querySelectorAll('.status-item .status-value')[1],
        version: document.querySelectorAll('.status-item .status-value')[2],
        ram: document.querySelectorAll('.status-item .status-value')[3],
        cpu: document.querySelectorAll('.status-item .status-value')[4],
    };
    const botControls = document.querySelector('.bot-controls');
    if (!botControls) {
        return;
    }
    botControls.style.flexDirection = 'column';
    botControls.style.gap = '12px';
    // Selecciona los botones ya definidos en el HTML
    const startBtn = botControls.querySelector('.start-button');
    const restartBtn = botControls.querySelector('.restart-button');
    const stopBtn = botControls.querySelector('.danger-button');
    if (!startBtn || !restartBtn || !stopBtn) {
        return;
    }
    async function updateBotStatus() {
        try {
            const res = await fetch('/api/bot/status');
            if (!res.ok) {
                throw new Error('No se pudo obtener el estado del bot');
            }
            const data = await res.json();
            botStatusFields.estado.textContent = data.estado;
            if (data.estado === 'ONLINE') {
                botStatusFields.estado.className = 'status-value online';
            } else if (data.estado === 'OFFLINE') {
                botStatusFields.estado.className = 'status-value offline';
            } else {
                botStatusFields.estado.className = 'status-value error';
            }
            botStatusFields.tiempo.textContent = data.tiempoActivo || '-';
            botStatusFields.version.textContent = data.version || '-';
            botStatusFields.ram.textContent = data.ram || '-';
            botStatusFields.cpu.textContent = data.cpu || '-';
            if (data.estado === 'ONLINE') {
                startBtn.style.display = 'none';
                restartBtn.style.display = '';
                stopBtn.style.display = '';
                restartBtn.disabled = false;
                stopBtn.disabled = false;
            } else {
                startBtn.style.display = '';
                restartBtn.style.display = 'none';
                stopBtn.style.display = 'none';
                startBtn.disabled = false;
            }
        } catch (e) {
            botStatusFields.estado.textContent = 'Desconocido';
            botStatusFields.estado.className = 'status-value error';
            botStatusFields.tiempo.textContent = '-';
            botStatusFields.version.textContent = '-';
            botStatusFields.ram.textContent = '-';
            botStatusFields.cpu.textContent = '-';
            startBtn.style.display = '';
            restartBtn.style.display = 'none';
            stopBtn.style.display = 'none';
            startBtn.disabled = false;
        }
    }
    startBtn.onclick = async (e) => {
        e?.preventDefault?.();
        startBtn.disabled = true;
        showNotification('Iniciando bot...', 'info');
        await fetch('/api/bot/start', { method: 'POST' });
        setTimeout(updateBotStatus, 1000);
    };
    restartBtn.onclick = async (e) => {
        e?.preventDefault?.();
        restartBtn.disabled = true;
        showNotification('Reiniciando bot...', 'info');
        await fetch('/api/bot/restart', { method: 'POST' });
        setTimeout(updateBotStatus, 2000);
    };
    stopBtn.onclick = async (e) => {
        e?.preventDefault?.();
        stopBtn.disabled = true;
        showNotification('Deteniendo bot...', 'warning');
        await fetch('/api/bot/stop', { method: 'POST' });
        setTimeout(updateBotStatus, 1000);
    };
    if (!botControls._interval) {
        botControls._interval = setInterval(updateBotStatus, 1000);
    }
    updateBotStatus();
}

// Llama a la inicialización cada vez que se carga el dashboard
initDashboardBotControls();
// =====================
// Logs recientes
const logList = document.getElementById('recent-logs-list');
async function updateRecentLogs() {
    try {
        const res = await fetch('/api/logs?limit=10');
        if (!res.ok) {
            throw new Error('No se pudieron obtener los logs');
        }
        const logs = await res.json();
        logList.innerHTML = '';
        let count = 0;
        logs.forEach(log => {
            const match = log.match(/^\d{4}-\d{2}-\d{2} (\d{2}:\d{2}:\d{2})\s+(INFO|WARN|ERROR)\s+[^-]+-\s+(.*)$/i);
            let time = '', level = '', message = '';
            if (match) {
                time = match[1];
                level = match[2].toLowerCase();
                message = match[3].trim();
            } else {
                level = 'info';
                time = '';
                message = log;
            }
            if (level === 'debug') {
                return;
            }
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

// =====================
// Integraciones (JDA, AI API, Base de datos)
const integracionesContainer = document.createElement('div');
integracionesContainer.className = 'dashboard-card';
integracionesContainer.innerHTML = `
    <div class="card-header">
        <i class="fas fa-plug"></i>
        <h3>Integraciones</h3>
    </div>
    <div class="card-content" id="integraciones-content">
        <div class="integracion-item"><span class="integracion-label">JDA:</span> <span id="jda-status">-</span> <span id="jda-ping"></span></div>
        <div class="integracion-item"><span class="integracion-label">AI API:</span> <span id="ai-status">-</span> <span id="ai-msg"></span></div>
        <div class="integracion-item"><span class="integracion-label">Base de datos:</span> <span id="db-status">-</span> <span id="db-ping"></span></div>
    </div>
`;

// Insertar la tarjeta de integraciones en el dashboard (después de las estadísticas)
window.addEventListener('DOMContentLoaded', () => {
    const dashboardGrid = document.querySelector('.dashboard-grid');
    if (dashboardGrid) {
        dashboardGrid.insertBefore(integracionesContainer, dashboardGrid.children[2] || null);
    }
});

async function updateIntegraciones() {
    try {
        const res = await fetch('/api/bot/integraciones');
        if (!res.ok) throw new Error('No se pudo obtener el estado de integraciones');
        const data = await res.json();
        // JDA
        document.getElementById('jda-status').textContent = data.jda.conectado ? 'Conectado ✅' : 'Desconectado ❌';
        document.getElementById('jda-ping').textContent = data.jda.ping >= 0 ? `| Ping: ${data.jda.ping} ms` : '';
        // AI API
        document.getElementById('ai-status').textContent = data.aiApi.disponible ? 'Disponible ✅' : 'No disponible ❌';
        document.getElementById('ai-msg').textContent = data.aiApi.mensaje ? `| ${data.aiApi.mensaje}` : '';
        // Base de datos
        document.getElementById('db-status').textContent = data.database.conectada ? 'Conectada ✅' : 'Desconectada ❌';
        document.getElementById('db-ping').textContent = data.database.ping >= 0 ? `| Ping: ${data.database.ping} ms` : '';
    } catch (e) {
        document.getElementById('jda-status').textContent = 'Desconocido';
        document.getElementById('jda-ping').textContent = '';
        document.getElementById('ai-status').textContent = 'Desconocido';
        document.getElementById('ai-msg').textContent = '';
        document.getElementById('db-status').textContent = 'Desconocido';
        document.getElementById('db-ping').textContent = '';
    }
}
setInterval(updateIntegraciones, 2000);
updateIntegraciones();
