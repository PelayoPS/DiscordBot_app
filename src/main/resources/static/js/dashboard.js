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
    if (logList) {
        if (logList._interval) {
            clearInterval(logList._interval);
        }
        logList._interval = setInterval(updateRecentLogs, 2000);
        updateRecentLogs();
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
