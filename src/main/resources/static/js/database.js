
(function() {
// Variables "privadas" dentro del IIFE
let currentTable = null;
let tables = [];
let tableData = {};
let tableColumns = {};

// Inicialización robusta: espera a que #database-screen esté en el DOM
function waitForDatabaseScreen() {
    if (document.getElementById('database-screen')) {
        initializeDatabaseView().then(setupEventListeners);
    } else {
        setTimeout(waitForDatabaseScreen, 30);
    }
}
waitForDatabaseScreen();

// Funciones principales
async function initializeDatabaseView() {
    try {
        // 1. Obtener lista de tablas
        const response = await fetch('/api/db/tables');
        if (!response.ok) {
          throw new Error('Error al obtener las tablas');
        }
        tables = await response.json();
        console.log('[DB] Tablas obtenidas:', tables);

        // 2. Crear las pestañas
        createTabs();
        console.log('[DB] Pestañas creadas');

        // 3. Cargar la primera tabla por defecto
        if (tables.length > 0) {
            console.log('[DB] Cargando tabla por defecto:', tables[0]);
            await switchTable(tables[0]);
        } else {
            console.warn('[DB] No hay tablas para mostrar');
        }
    } catch (error) {
        console.error('Error al inicializar la vista:', error);
        showNotification('Error al cargar las tablas de la base de datos', 'error');
    }
}

function setupEventListeners() {
    // Búsqueda
    document.getElementById('search-input').addEventListener('input', handleSearch);
}

function createTabs() {
    const tabsContainer = document.getElementById('database-tabs');
    tabsContainer.innerHTML = '';
    console.log('[DB] Creando tabs para:', tables);
    tables.forEach((tableName, index) => {
        const tab = document.createElement('div');
        tab.className = `tab ${index === 0 ? 'active' : ''}`;
        tab.setAttribute('data-table', tableName);
        tab.textContent = formatTableName(tableName);
        tab.addEventListener('click', () => switchTable(tableName));
        tabsContainer.appendChild(tab);
    });
    console.log('[DB] Tabs en el DOM:', tabsContainer.innerHTML);
}

async function switchTable(tableName) {
    try {
        console.log('[DB] switchTable:', tableName);
        // 1. Actualizar pestaña activa
        document.querySelectorAll('.tab').forEach(tab => {
            tab.classList.toggle('active', tab.getAttribute('data-table') === tableName);
        });

        // 2. Obtener columnas si no las tenemos
        if (!tableColumns[tableName]) {
            const columnsResponse = await fetch(`/api/db/tables/${tableName}/columns`);
            if (!columnsResponse.ok) {
              throw new Error('Error al obtener las columnas');
            }
            tableColumns[tableName] = await columnsResponse.json();
            console.log('[DB] Columnas obtenidas:', tableColumns[tableName]);
        }

        // 3. Obtener datos de la tabla
        const dataResponse = await fetch(`/api/db/tables/${tableName}/data`);
        if (!dataResponse.ok) {
          throw new Error('Error al obtener los datos');
        }
        let data = await dataResponse.json();
        // Si la respuesta es un objeto con 'data' y 'columns', usar solo 'data'
        if (data && typeof data === 'object' && !Array.isArray(data) && data.data) {
            tableData[tableName] = data.data;
        } else {
            tableData[tableName] = data;
        }
        console.log('[DB] Datos obtenidos:', tableData[tableName]);

        // 4. Renderizar la tabla
        renderTable(tableName);
        currentTable = tableName;
    } catch (error) {
        console.error('Error al cambiar de tabla:', error);
        showNotification('Error al cargar los datos de la tabla', 'error');
    }
}

function renderTable(tableName) {

    const container = document.getElementById('tables-container');
    const columns = tableColumns[tableName] || [];
    let data = tableData[tableName] || [];
    console.log('[DB] renderTable:', { tableName, columns, data });

    // Filtrar usuarios inválidos si es la tabla 'usuarios'
    if (tableName.toLowerCase() === 'usuarios') {
        data = data.filter(row => {
            // tipo_usuario no nulo/undefined/vacío y nivel > 0
            const tipo = row['tipo_usuario'];
            const nivel = row['nivel'];
            return tipo !== null && tipo !== undefined && String(tipo).trim() !== '' && Number(nivel) > 0;
        });
        console.log('[DB] Usuarios válidos tras filtro:', data);
    }

    // Limpiar contenedor
    container.innerHTML = '';

    // Mapeo de nombres de columna a etiquetas amigables
    const friendlyLabels = {
        'id_usuario': 'Usuario Discord',
        'tipo_usuario': 'Tipo',
        'nivel': 'Nivel',
        'puntos_xp': 'XP',
        'timestamp_ultimo_mensaje': 'Último mensaje',
        'id': 'ID',
        'nombre': 'Nombre',
        'registrado': 'Registrado',
        'ultimo_acceso': 'Último acceso',
        'usuario': 'Usuario',
        'servidor': 'Servidor',
        'tipo': 'Tipo',
        'razon': 'Razón',
        'moderador': 'Moderador',
        'fecha': 'Fecha',
        'hora': 'Hora',
    };

    // Si no hay columnas, mostrar mensaje de error
    if (!columns.length) {
        container.innerHTML = '<div class="no-data-message">No se pudieron cargar las columnas de la tabla.</div>';
        return;
    }

    // Si no hay datos, mostrar mensaje de "No hay datos"
    if (!data.length) {
        const table = document.createElement('table');
        table.className = 'database-table';
        const thead = document.createElement('thead');
        const headerRow = document.createElement('tr');
        columns.forEach(column => {
            const th = document.createElement('th');
            th.textContent = friendlyLabels[column.name] || column.label || column.name;
            headerRow.appendChild(th);
        });
        thead.appendChild(headerRow);
        table.appendChild(thead);
        // Mensaje de no hay datos
        const tbody = document.createElement('tbody');
        const tr = document.createElement('tr');
        const td = document.createElement('td');
        td.colSpan = columns.length;
        td.className = 'no-data-message';
        td.textContent = 'No hay datos en esta tabla.';
        tr.appendChild(td);
        tbody.appendChild(tr);
        table.appendChild(tbody);
        container.appendChild(table);
        return;
    }

    // Crear estructura de la tabla
    const table = document.createElement('table');
    table.className = 'database-table';

    // Crear encabezado
    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');
    columns.forEach(column => {
        const th = document.createElement('th');
        th.textContent = friendlyLabels[column.name] || column.label || column.name;
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);
    table.appendChild(thead);

    // Crear cuerpo de la tabla
    const tbody = document.createElement('tbody');
    data.forEach(row => {
        const tr = document.createElement('tr');
        columns.forEach(column => {
            const td = document.createElement('td');
            let value = row[column.name];
            // Forzar id_usuario a string siempre
            if (column.name === 'id_usuario' && value != null) {
                value = String(value);
            }
            // Formato especial para timestamp
            if (column.name === 'timestamp_ultimo_mensaje' && value) {
                try {
                    const date = new Date(Number(value));
                    value = date.toLocaleString();
                } catch (e) {}
            }
            // Edición inline solo para usuarios.nivel y usuarios.puntos_xp
            if (
                tableName.toLowerCase() === 'usuarios' &&
                (column.name === 'nivel' || column.name === 'puntos_xp')
            ) {
                td.contentEditable = true;
                td.classList.add('editable-cell');
                td.textContent = value != null ? value : '';
                td.addEventListener('blur', async function (e) {
                    const newValue = td.textContent.trim();
                    if (newValue === String(row[column.name])) return;
                    // Validación básica: solo números
                    if (isNaN(newValue) || newValue === '') {
                        showNotification('Introduce un valor numérico válido', 'warning');
                        td.textContent = row[column.name];
                        return;
                    }
                    // Actualizar en backend
                    try {
                        // El id debe ser string y obligatorio
                        const id = String(row['id_usuario']);
                        if (!id) throw new Error('ID de usuario no encontrado');
                        // Prueba GET para debug
                        const testRes = await fetch(`/api/user/${id}`);
                        if (!testRes.ok) {
                            showNotification('El usuario no existe en el backend (GET /api/user/' + id + ')', 'error');
                            td.textContent = row[column.name];
                            return;
                        }
                        const payload = {};
                        payload[column.name] = Number(newValue);
                        const res = await fetch(`/api/db/experiencia/${id}`, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(payload),
                        });
                        if (!res.ok) throw new Error('Error al actualizar');
                        row[column.name] = Number(newValue);
                        showNotification('Actualizado correctamente', 'success');
                    } catch (err) {
                        showNotification('Error al actualizar: ' + err.message, 'error');
                        td.textContent = row[column.name];
                    }
                });
            } else {
                td.textContent = value != null ? value : '';
            }
            tr.appendChild(td);
        });
        tbody.appendChild(tr);
    });
    table.appendChild(tbody);
    container.appendChild(table);
}

// Funciones auxiliares
function createActionButton(icon, onClick) {
    const button = document.createElement('button');
    button.className = 'icon-button';
    button.innerHTML = `<i class="fas fa-${icon}"></i>`;
    button.addEventListener('click', onClick);
    return button;
}

function formatTableName(name) {
    return name.charAt(0).toUpperCase() + name.slice(1).toLowerCase().replace(/_/g, ' ');
}

function handleSearch(event) {
    const searchTerm = event.target.value.toLowerCase();
    const rows = document.querySelectorAll('.database-table tbody tr');

    rows.forEach(row => {
        const text = Array.from(row.cells)
            .map(cell => cell.textContent.toLowerCase())
            .join(' ');
        row.style.display = text.includes(searchTerm) ? '' : 'none';
    });
}

function showNotification(message, type = 'info') {
    // Implementar según el sistema de notificaciones existente
    console.log(`${type}: ${message}`);
}

// Funciones de gestión de experiencia (no usadas en la UI actual, pero se mantienen privadas)
async function handleEdit(row) {}
function closeModal() {}
async function handleSave() {}
async function handleDelete(row) {}
function handleNewRecord() {}

})();