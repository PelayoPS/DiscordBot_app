// Inicialización de la pantalla de logs
window.initLogsScreen = function () {
    const filterToggles = document.querySelectorAll('.filter-toggle');
    const dateInputs = document.querySelectorAll('.date-filter input[type="date"]');
    const logsTableBody = document.querySelector('.logs-table tbody');
    const reloadBtn = document.getElementById('reload-logs-btn');
    let currentPage = 1;
    const logsPerPage = 20;
    let allLogsRaw = [];
    let allLogsParsed = [];

    // Establecer la fecha de hoy por defecto si está vacío
    const today = new Date().toISOString().slice(0, 10);
    dateInputs.forEach(input => {
        if (!input.value) {
            input.value = today;
        }
    });

    // Función para animar la recarga
    function animateReload() {
        if (reloadBtn) {
            reloadBtn.classList.add('reloading');
            const icon = reloadBtn.querySelector('i');
            if (icon) {
                icon.style.transform = 'rotate(360deg)';
            }
            setTimeout(() => {
                reloadBtn.classList.remove('reloading');
                if (icon) {
                    icon.style.transform = '';
                }
            }, 1000);
        }
    }

    function getActiveTypes() {
        // Devuelve los tipos activos en mayúsculas para comparación case-insensitive
        return Array.from(filterToggles)
            .filter(btn => btn.classList.contains('active'))
            .map(btn => btn.getAttribute('data-type').toUpperCase());
    }

    // Función para animar la transición de los filtros
    function animateFilterTransition(element) {
        element.style.transition = 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)';
        element.style.transform = 'scale(0.95)';
        setTimeout(() => {
            element.style.transform = 'scale(1)';
        }, 150);
    }

    // Usar la función de utilidades global para parsear una línea de log
    function parseLogLine(line) {
        return window.logUtils.parseLogEntry(line);
    }

    // Agrupar líneas de stacktrace antes de parsear
    function groupLogLines(rawLines) {
        const grouped = [];
        const logStartRegex = /^(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}:\d{2})/;
        let current = '';
        rawLines.forEach(line => {
            if (logStartRegex.test(line)) {
                if (current) grouped.push(current);
                current = line;
            } else {
                // Línea de stacktrace o continuación
                current += '\n' + line;
            }
        });
        if (current) grouped.push(current);
        return grouped;
    }

    // Función para filtrar logs por fecha y tipo
    function filterLogs() {
        const startDate = document.querySelector('#start-date')?.value;
        const endDate = document.querySelector('#end-date')?.value;
        const activeTypes = getActiveTypes();
        return allLogsParsed.filter(log => {
            if (!log.isParsed) return true; // Mostrar líneas no parseadas
            let fechaOk = true;
            let tipoOk = true;
            // Comparar fechas como strings YYYY-MM-DD
            if (startDate) fechaOk = log.fecha >= startDate;
            if (endDate) fechaOk = fechaOk && log.fecha <= endDate;
            if (activeTypes.length > 0) tipoOk = activeTypes.includes(log.tipo.toUpperCase());
            return fechaOk && tipoOk;
        });
    }

    // Función para cargar todos los logs del backend
    async function fetchAllLogs() {
        try {
            animateReload();
            const response = await fetch('/api/logs?limit=0');
            if (!response.ok) throw new Error('Error al cargar los logs');
            const data = await response.json();
            // Usar la función automática para filtrar solo las primeras líneas de cada bloque
            allLogsParsed = window.logUtils.parseMainLogLines(data);
            allLogsRaw = data; // Guardar el array original por si se necesita
        } catch (error) {
            console.error('Error:', error);
            allLogsRaw = [];
            allLogsParsed = [];
        }
    }

    // Función para obtener el color del nivel
    function getLevelColor(tipo) {
        if (window.logUtils?.getLogTypeColor) {
            return window.logUtils.getLogTypeColor(tipo);
        }
        // Fallback por tipo
        switch (tipo) {
            case 'INFO': return '#1976d2';
            case 'WARN': return '#ff9800';
            case 'ERROR': return '#e53935';
            case 'DEBUG': return '#747f8d';
            default: return '#5865f2';
        }
    }

    // Función para renderizar los logs con animaciones y paginación
    function renderLogsPage(page) {
        const filteredLogs = filterLogs();
        const totalPages = Math.max(1, Math.ceil(filteredLogs.length / logsPerPage));
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;
        currentPage = page;
        const startIdx = (currentPage - 1) * logsPerPage;
        const endIdx = startIdx + logsPerPage;
        const logsToShow = filteredLogs.slice(startIdx, endIdx);

        if (logsToShow.length === 0) {
            logsTableBody.innerHTML = '<tr><td colspan="5" class="no-logs-message">No hay logs disponibles</td></tr>';
        } else {
            logsTableBody.style.opacity = '0';
            setTimeout(() => {
                logsTableBody.innerHTML = '';
                logsTableBody.style.opacity = '1';
                logsToShow.forEach((logObject, index) => {
                    const row = document.createElement('tr');
                    row.classList.add('log-row-detailed');
                    if (logObject.isParsed && logObject.tipo) row.classList.add(logObject.tipo.toLowerCase());
                    row.style.animationDelay = `${index * 50}ms`;
                    row.style.opacity = '0';
                    row.style.transform = 'translateY(10px)';
                    if (!logObject.isParsed) {
                        row.innerHTML = `<td class="log-cell-main" colspan="5">${logObject.raw}</td>`;
                    } else {
                        let levelColor = getLevelColor(logObject.tipo);
                        row.innerHTML = `
                            <td class="log-cell-fecha">${logObject.fecha}</td>
                            <td class="log-cell-hora">${logObject.hora}</td>
                            <td class="log-cell-nivel">
                                <span class="log-level-tag" style="background-color: ${levelColor};">${logObject.tipo}</span>
                            </td>
                            <td class="log-cell-origen">${logObject.origen}</td>
                            <td class="log-cell-mensaje">${logObject.mensaje}</td>
                        `;
                    }
                    logsTableBody.appendChild(row);
                    row.offsetHeight;
                    row.style.transition = 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)';
                    row.style.opacity = '1';
                    row.style.transform = 'translateY(0)';
                });
            }, 200);
        }
        updatePagination(totalPages);
    }

    // Función para actualizar la paginación con animaciones
    function updatePagination(totalPages) {
        const paginationContainer = document.querySelector('.pagination');
        const paginationInfo = document.querySelector('.pagination-info');
        const prevBtn = paginationContainer?.querySelector('.pagination-button:first-child');
        const nextBtn = paginationContainer?.querySelector('.pagination-button:last-child');
        if (!paginationContainer || !paginationInfo || !prevBtn || !nextBtn) {
            if (paginationContainer) paginationContainer.style.display = 'none';
            if (paginationInfo) paginationInfo.textContent = '';
            return;
        }
        paginationContainer.style.opacity = '0';
        setTimeout(() => {
            if (totalPages > 0) {
                paginationContainer.style.display = 'flex';
                paginationInfo.textContent = `Página ${currentPage} de ${totalPages}`;
            } else {
                paginationContainer.style.display = 'none';
                paginationInfo.textContent = '';
                return;
            }
            prevBtn.disabled = currentPage === 1;
            nextBtn.disabled = currentPage === totalPages;
            prevBtn.onclick = () => {
                if (currentPage > 1) {
                    prevBtn.style.transform = 'scale(0.95)';
                    setTimeout(() => {
                        prevBtn.style.transform = 'scale(1)';
                        renderLogsPage(currentPage - 1);
                    }, 150);
                }
            };
            nextBtn.onclick = () => {
                if (currentPage < totalPages) {
                    nextBtn.style.transform = 'scale(0.95)';
                    setTimeout(() => {
                        nextBtn.style.transform = 'scale(1)';
                        renderLogsPage(currentPage + 1);
                    }, 150);
                }
            };
            paginationContainer.style.opacity = '1';
        }, 200);
    }

    // Eventos para los filtros
    filterToggles.forEach(toggle => {
        toggle.addEventListener('click', function() {
            this.classList.toggle('active');
            animateFilterTransition(this);
            currentPage = 1;
            renderLogsPage(currentPage);
        });
    });

    // Eventos para inputs de fecha con animaciones
    dateInputs.forEach(input => {
        input.addEventListener('change', () => {
            input.style.transform = 'scale(0.98)';
            setTimeout(() => {
                input.style.transform = 'scale(1)';
                currentPage = 1;
                renderLogsPage(currentPage);
            }, 150);
        });
    });

    // Evento para el botón de recargar
    if (reloadBtn) {
        reloadBtn.addEventListener('click', async () => {
            currentPage = 1;
            await fetchAllLogs();
            renderLogsPage(currentPage);
        });
    }

    // Cargar logs iniciales
    (async () => {
        await fetchAllLogs();
        renderLogsPage(currentPage);
    })();
};

// Limpieza de recursos de la pantalla de logs
window.destroyLogsScreen = function () {
    const reloadBtn = document.getElementById('reload-logs-btn');
    if (reloadBtn) {
        reloadBtn.replaceWith(reloadBtn.cloneNode(true));
    }
};
