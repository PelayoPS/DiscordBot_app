// logUtils.js: Utilidades para el manejo de logs

window.logUtils = {
    parseLogEntry: function(entry) {
        // Ejemplo: 2025-05-06 17:47:45 INFO bot.log.LoggingManager - Base de datos ...
        const regex = /^(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}:\d{2}) (\w+) ([^\-]+)- (.*)$/;
        const match = entry.match(regex);
        if (!match) {
            // Si no coincide, devolver todo como mensaje
            return {
                raw: entry, // Guardar la entrada original
                fecha: '',
                hora: '',
                tipo: 'INFO', // Default type, normalizado
                origen: '',
                mensaje: entry,
                isParsed: false
            };
        }
        return {
            raw: entry, // Guardar la entrada original
            fecha: match[1],
            hora: match[2],
            tipo: match[3].toUpperCase(), // Normalizar a mayúsculas para la comparación
            origen: match[4].trim(),
            mensaje: match[5],
            isParsed: true
        };
    },

    // Podríamos añadir más utilidades aquí en el futuro, como la lógica de colores.
    getLogTypeColor: function(type) {
        let levelColor = '#5865f2'; // Color por defecto (INFO)
        switch (type.toUpperCase()) {
            case 'ERROR':
                levelColor = '#ed4245'; // Rojo
                break;
            case 'WARN': // 'WARN' es el tipo normalizado
                levelColor = '#faa61a'; // Naranja
                break;
            case 'DEBUG':
                levelColor = '#72767d'; // Gris
                break;
            case 'TRACE':
                levelColor = '#43b581'; // Verde (ejemplo, puedes ajustarlo)
                break;
        }
        return levelColor;
    }
};
