// Inicialización de la pantalla de logs
window.initLogsScreen = function () {
    const logFilterCheckboxes = document.querySelectorAll('.filter-checkbox input');
    const dateInputs = document.querySelectorAll('.date-filter input[type="date"]');
    const logsTableBody = document.querySelector('.logs-table tbody');

    // Establecer la fecha de hoy por defecto si está vacío
    const today = new Date().toISOString().slice(0, 10);
    dateInputs.forEach(input => {
        if (!input.value) {
            input.value = today;
        }
    });

    async function fetchAndRenderLogs() {
        // Tipos seleccionados
        const types = Array.from(logFilterCheckboxes)
            .filter(cb => cb.checked)
            .map(cb => cb.nextElementSibling.classList[1]);
        // Fechas
        const fromDate = dateInputs[0].value;
        const toDate = dateInputs[1].value;
        // Construir query
        const params = new URLSearchParams();
        if (fromDate) {
            params.append('from', fromDate);
        }
        if (toDate) {
            params.append('to', toDate);
        }
        if (types.length > 0) {
            types.forEach(t => params.append('types', t.toUpperCase()));
        }
        params.append('limit', '1000');
        try {
            const res = await fetch(`/api/logs?${params.toString()}`);
            const logs = await res.json();
            renderLogs(logs);
        } catch (e) {
            logsTableBody.innerHTML = '<tr><td colspan="5">Error al cargar logs</td></tr>';
        }
    }

    function parseLogEntries(lines) {
        // Agrupa líneas en entradas de log completas (incluyendo stack traces)
        const entries = [];
        let current = [];
        const logHeaderRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2} (INFO|WARN|ERROR|DEBUG)\s+[^\s]+ - /;
        for (const line of lines) {
            if (logHeaderRegex.test(line)) {
                if (current.length) {
                    entries.push(current.join('\n'));
                }
                current = [line];
            }
            else if (current.length) {
                current.push(line);
            }
        }
        if (current.length) {
            entries.push(current.join('\n'));
        }
        return entries;
    }

    function renderLogs(logs) {
        if (!logs || logs.length === 0) {
            logsTableBody.innerHTML = '<tr><td colspan="5">No hay logs para los filtros seleccionados</td></tr>';
            return;
        }
        // Agrupar logs multilinea
        const entries = parseLogEntries(logs);
        // --- Paginación local ---
        let currentPage = 1;
        const pageSize = 20;
        let totalPages = Math.ceil(entries.length / pageSize);
        // Usar el paginador original del HTML
        let paginationContainer = document.querySelector('.pagination');
        let paginationInfo = document.querySelector('.pagination-info');
        let prevBtn = paginationContainer.querySelectorAll('.pagination-button')[0];
        let nextBtn = paginationContainer.querySelectorAll('.pagination-button')[1];
        function renderPage(page) {
            currentPage = page;
            const start = (page - 1) * pageSize;
            const end = start + pageSize;
            const pageEntries = entries.slice(start, end);
            logsTableBody.innerHTML = pageEntries.map(entry => {
                // Expresión regular corregida para parsear logs tipo:
                // 2024-05-04 12:34:56 INFO com.example.Clase - Mensaje
                const match = entry.match(/^(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}:\d{2}) (INFO|WARN|ERROR|DEBUG)\s+([^\s]+) - ([\s\S]*)$/);
                if (!match) {
                    return '';
                }
                const [_, fecha, hora, tipo, origen, mensaje] = match;
                const mensajeHtml = mensaje.replace(/\n/g, '<br>');
                return `<tr class="log-row ${tipo.toLowerCase()}"><td>${fecha.split('-').reverse().join('/')}</td><td>${hora}</td><td><span class="log-level ${tipo.toLowerCase()}">${tipo}</span></td><td>${mensajeHtml}</td><td>${origen}</td></tr>`;
            }).join('');
            renderPagination();
        }
        function renderPagination() {
            // Actualiza la info de página
            if (paginationInfo) {
                paginationInfo.textContent = `Página ${currentPage} de ${totalPages}`;
            }
            // Botón anterior
            prevBtn.disabled = currentPage === 1;
            prevBtn.onclick = () => {
                if (currentPage > 1) renderPage(currentPage - 1);
            };
            // Botón siguiente
            nextBtn.disabled = currentPage === totalPages;
            nextBtn.onclick = () => {
                if (currentPage < totalPages) renderPage(currentPage + 1);
            };
        }
        renderPage(1);
    }

    logFilterCheckboxes.forEach(cb => cb.addEventListener('change', fetchAndRenderLogs));
    dateInputs.forEach(input => input.addEventListener('change', fetchAndRenderLogs));
    fetchAndRenderLogs();
};

// Limpieza de recursos de la pantalla de logs
window.destroyLogsScreen = function () {
};
