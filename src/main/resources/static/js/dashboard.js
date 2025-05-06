// dashboard.js: Lógica JS específica para el dashboard
let dashboardPingInterval = null;

// Encapsula toda la lógica en una función global para carga dinámica
window.initDashboardBotControls = function() {
    // Lógica del dashboard: estado del bot y logs recientes
    function getStatusValue(label) {
        const items = document.querySelectorAll('.estado-bot .integracion-item');
        for (const item of items) {
            const labelElem = item.querySelector('.integracion-label');
            if (labelElem && labelElem.textContent.trim().toLowerCase().includes(label)) {
                return item.querySelector('.status-value');
            }
        }
        return null;
    }
    const botStatusFields = {
        estado: getStatusValue('estado'),
        tiempo: getStatusValue('tiempo'),
        version: getStatusValue('versión'),
        ram: getStatusValue('ram'),
        cpu: getStatusValue('cpu'),
    };
    const botControls = document.querySelector('.bot-controls');
    if (!botControls) {
        return;
    }
    botControls.style.flexDirection = 'column';
    botControls.style.gap = '12px';
    const startBtn = botControls.querySelector('.start-button');
    const restartBtn = botControls.querySelector('.restart-button');
    const stopBtn = botControls.querySelector('.stop-button');
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
            if (botStatusFields.estado) {
                botStatusFields.estado.textContent = data.estado;
                botStatusFields.estado.className = 'status-value ' + (data.estado === 'ONLINE' ? 'online' : (data.estado === 'OFFLINE' ? 'offline' : 'error'));
            }
            if (botStatusFields.tiempo) {
                botStatusFields.tiempo.textContent = data.tiempoActivo || '-';
            }
            if (botStatusFields.version) {
                botStatusFields.version.textContent = data.version || '-';
            }
            if (botStatusFields.ram) {
                botStatusFields.ram.textContent = data.ram || '-';
            }
            if (botStatusFields.cpu) {
                botStatusFields.cpu.textContent = data.cpu || '-';
            }
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
            if (botStatusFields.estado) {
                botStatusFields.estado.textContent = 'Desconocido';
                botStatusFields.estado.className = 'status-value error';
            }
            if (botStatusFields.tiempo) {
                botStatusFields.tiempo.textContent = '-';
            }
            if (botStatusFields.version) {
                botStatusFields.version.textContent = '-';
            }
            if (botStatusFields.ram) {
                botStatusFields.ram.textContent = '-';
            }
            if (botStatusFields.cpu) {
                botStatusFields.cpu.textContent = '-';
            }
            startBtn.style.display = '';
            restartBtn.style.display = 'none';
            stopBtn.style.display = 'none';
            startBtn.disabled = false;
        }
    }
    startBtn.onclick = async (e) => {
        if (e) {
            e.preventDefault();
        }
        startBtn.disabled = true;
        showNotification('Iniciando bot...', 'info');
        await fetch('/api/bot/start', { method: 'POST' });
        setTimeout(updateBotStatus, 1000);
    };
    restartBtn.onclick = async (e) => {
        if (e) {
            e.preventDefault();
        }
        restartBtn.disabled = true;
        showNotification('Reiniciando bot...', 'info');
        await fetch('/api/bot/restart', { method: 'POST' });
        setTimeout(updateBotStatus, 2000);
    };
    stopBtn.onclick = async (e) => {
        if (e) {
            e.preventDefault();
        }
        stopBtn.disabled = true;
        showNotification('Deteniendo bot...', 'warning');
        await fetch('/api/bot/stop', { method: 'POST' });
        setTimeout(updateBotStatus, 1000);
    };
    if (!botControls._interval) {
        botControls._interval = setInterval(updateBotStatus, 1000);
    }
    updateBotStatus();

    // Logs recientes
    const logList = document.getElementById('recent-logs-list');
    let logPollInterval = null;
    let logItems = [];
    const MAX_LOGS = 5;
    function renderLogs(logs) {
        logList.innerHTML = '';
        let count = 0;
        logs.forEach(log => {
            // Regex: fecha, nivel, origen, mensaje
            const match = log.match(/^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})\s+(INFO|WARN|ERROR|DEBUG)\s+([^\s]+)\s*-\s*([\s\S]*)$/i);
            let fecha = '', nivel = '', origen = '', mensaje = '';
            if (match) {
                fecha = match[1];
                nivel = match[2].toUpperCase();
                origen = match[3];
                mensaje = match[4].trim();
            } else {
                mensaje = log;
                nivel = 'INFO';
            }
            if (count >= MAX_LOGS) {
              return;
            }
            count++;
            const logEntryDiv = document.createElement('div');
            logEntryDiv.className = 'log-entry';
            logEntryDiv.style.padding = '4px 0';
            let levelColor = '#5865f2';
            switch (nivel.toLowerCase()) {
                case 'debug': levelColor = '#43b581'; break;
                case 'warn': case 'warning': levelColor = '#faa61a'; break;
                case 'error': levelColor = '#ed4245'; break;
            }
            logEntryDiv.innerHTML = `
                <div class="log-header" style="display: flex; align-items: center; gap: 8px; margin-bottom: 2px;">
                    <span class="log-timestamp" style="color: #8e9297; font-size: 12px;">${fecha}</span>
                    <span class="log-level-tag" style="color: white; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: bold; background-color: ${levelColor};">${nivel}</span>
                    <span class="log-logger" style="color: #aaa; font-size: 12px;">${origen}</span>
                </div>
                <div class="log-message-content" style="color: #b9bbbe; font-size: 13px; word-wrap: break-word; line-height: 1.4;">
                    ${mensaje}
                </div>
            `;
            logList.appendChild(logEntryDiv);
        });
        if (count === 0) {
            logList.innerHTML = '<div class="log-item log-info" style="color: #b9bbbe; font-size: 13px; padding: 4px 0;">No hay logs recientes</div>';
        }
    }
    async function fetchLogs() {
        try {
            // Obtener la fecha de hoy en formato yyyy-MM-dd
            const today = new Date().toISOString().slice(0, 10);
            // Pedir los logs del día actual
            const res = await fetch(`/api/logs?limit=10&from=${today}&to=${today}`);
            if (!res.ok) {
                throw new Error('Error al obtener logs');
            }
            const logs = await res.json();
            logItems = logs;
            renderLogs(logItems);
        } catch (e) {
            logList.innerHTML = '<div class="log-item log-error">Error al cargar logs</div>';
        }
    }
    function startLogPolling() {
        if (!logList) {
            return;
        }
        if (logPollInterval) {
            clearInterval(logPollInterval);
        }
        fetchLogs();
        logPollInterval = setInterval(fetchLogs, 2000);
    }
    function stopLogPolling() {
        if (logPollInterval) {
            clearInterval(logPollInterval);
            logPollInterval = null;
        }
    }
    if (logList) {
        stopLogPolling();
        startLogPolling();
        if (!window._dashboardLogCleanup) {
            window._dashboardLogCleanup = true;
            window.addEventListener('beforeunload', stopLogPolling);
        }
    }

    // Integraciones (JDA, AI API, Base de datos)
    async function safeUpdateIntegraciones() {
        try {
            const res = await fetch('/api/bot/integraciones');
            if (!res.ok) {
                throw new Error('No se pudo obtener el estado de integraciones');
            }
            const data = await res.json();
            const jdaStatus = document.getElementById('jda-status');
            const jdaPing = document.getElementById('jda-ping');
            const aiStatus = document.getElementById('ai-status');
            const aiMsg = document.getElementById('ai-msg');
            const dbStatus = document.getElementById('db-status');
            const dbPing = document.getElementById('db-ping');
            if (jdaStatus) {
                jdaStatus.innerHTML = data.jda.conectado ? 'Conectado <span style="color:#43b581;font-weight:bold;">&#10004;</span>' : 'Desconectado <span style="color:#ed4245;font-weight:bold;">&#10008;</span>';
            }
            if (jdaPing) {
                jdaPing.textContent = data.jda.ping >= 0 ? `| Ping: ${data.jda.ping} ms` : '';
            }
            if (aiStatus) {
                aiStatus.innerHTML = data.aiApi.disponible ? 'Disponible <span style="color:#43b581;font-weight:bold;">&#10004;</span>' : 'No disponible <span style="color:#ed4245;font-weight:bold;">&#10008;</span>';
            }
            if (aiMsg) {
                aiMsg.textContent = data.aiApi.mensaje ? `| ${data.aiApi.mensaje}` : '';
            }
            let backendActivo = false;
            let backendPing = null;
            try {
                const start = performance.now();
                const backendRes = await fetch('/api/botfacade/ping');
                backendActivo = backendRes.ok;
                backendPing = Math.round(performance.now() - start);
            } catch (err) {
                backendActivo = false;
            }
            if (dbStatus) {
                dbStatus.innerHTML = backendActivo ? 'Backend activo <span style="color:#43b581;font-weight:bold;">&#10004;</span>' : 'Backend inactivo <span style="color:#ed4245;font-weight:bold;">&#10008;</span>';
            }
            if (dbPing) {
                dbPing.textContent = backendPing !== null ? `| Ping: ${backendPing} ms` : '';
            }
        } catch (e) {
            const jdaStatus = document.getElementById('jda-status');
            const jdaPing = document.getElementById('jda-ping');
            const aiStatus = document.getElementById('ai-status');
            const aiMsg = document.getElementById('ai-msg');
            const dbStatus = document.getElementById('db-status');
            const dbPing = document.getElementById('db-ping');
            if (jdaStatus) {
                jdaStatus.innerHTML = 'Desconocido';
            }
            if (jdaPing) {
                jdaPing.textContent = '';
            }
            if (aiStatus) {
                aiStatus.innerHTML = 'Desconocido';
            }
            if (aiMsg) {
                aiMsg.textContent = '';
            }
            if (dbStatus) {
                dbStatus.innerHTML = 'Desconocido';
            }
            if (dbPing) {
                dbPing.textContent = '';
            }
        }
    }
    clearInterval(window._integracionesInterval);
    window._integracionesInterval = setInterval(safeUpdateIntegraciones, 2000);
    safeUpdateIntegraciones();

    // Estadísticas de Base de Datos (DASHBOARD y tarjeta)
    function updateDatabaseStats() {
        fetch('/api/db/stats')
            .then(res => res.json())
            .then(data => {
                const dbItems = document.querySelectorAll('.dashboard-card.base-datos .integracion-item');
                if (dbItems.length >= 4) {
                    dbItems[0].querySelector('.db-count').textContent = data.userCount;
                    dbItems[1].querySelector('.db-count').textContent = data.experienceCount;
                    dbItems[2].querySelector('.db-count').textContent = data.penaltyCount;
                    const estadoSpan = dbItems[3].querySelector('.db-count');
                    estadoSpan.textContent = data.available ? 'Disponible' : 'No disponible';
                    estadoSpan.classList.remove('disponible', 'no-disponible');
                    estadoSpan.classList.add(data.available ? 'disponible' : 'no-disponible');
                }
            })
            .catch(() => {
                const dbItems = document.querySelectorAll('.dashboard-card.base-datos .integracion-item');
                if (dbItems.length >= 4) {
                    dbItems[0].querySelector('.db-count').textContent = '-';
                    dbItems[1].querySelector('.db-count').textContent = '-';
                    dbItems[2].querySelector('.db-count').textContent = '-';
                    const estadoSpan = dbItems[3].querySelector('.db-count');
                    estadoSpan.textContent = 'No disponible';
                    estadoSpan.classList.remove('disponible');
                    estadoSpan.classList.add('no-disponible');
                }
            });
    }
    setInterval(updateDatabaseStats, 2000);
    updateDatabaseStats();

    // Navegación rápida desde dashboard (Ver base de datos / Ver todos los logs)
    document.querySelectorAll('.view-all-link[data-screen]').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const screen = this.getAttribute('data-screen');
            if (!screen) {
                return;
            }
            // Actualiza el menú lateral
            document.querySelectorAll('.menu-item').forEach(item => {
                if (item.getAttribute('data-screen') === screen) {
                    item.classList.add('active');
                } else {
                    item.classList.remove('active');
                }
            });
            // Carga la pantalla usando la función global
            if (window.loadScreen) {
                window.loadScreen(screen);
            }
        });
    });

    // Ejemplo: iniciar ping periódico
    if (dashboardPingInterval) {
        clearInterval(dashboardPingInterval);
    }
    dashboardPingInterval = setInterval(() => {
        // Aquí iría la lógica de ping a integraciones
        // Por ejemplo: actualizar el estado de integraciones
        // updateIntegracionesStatus();
    }, 10000); // cada 10 segundos
};

// Limpieza de recursos del dashboard
window.destroyDashboardScreen = function() {
    if (dashboardPingInterval) {
        clearInterval(dashboardPingInterval);
        dashboardPingInterval = null;
    }
};
