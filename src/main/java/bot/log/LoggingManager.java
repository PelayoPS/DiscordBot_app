package bot.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para manejar el logging del sistema.
 * Permite registrar mensajes informativos y de error, y recuperar logs desde
 * archivo.
 * 
 * @author PelayoPS
 */
public class LoggingManager {

    private static final Logger logger = LoggerFactory.getLogger(LoggingManager.class);

    /**
     * Registra un mensaje informativo.
     * 
     * @param message El mensaje a registrar.
     */
    public void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Registra un mensaje de error.
     * 
     * @param message   El mensaje de error.
     * @param throwable La excepción asociada (opcional).
     */
    public void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    /**
     * Recupera los últimos logs del archivo de log, agrupando mensajes multilinea
     * (por entrada de log), y filtrando por nivel si se indica.
     *
     * @param level Nivel de log (INFO, WARN, ERROR, etc.)
     * @param limit Máximo número de entradas a devolver
     * @return Lista de bloques de log (cada bloque es una entrada completa,
     *         incluyendo stacktraces)
     */
    public List<String> getLogs(String level, int limit) {
        List<String> result = new ArrayList<>();
        File logFile = new File("logs/app.log");
        if (!logFile.exists())
            return result;
        String levelUpper = (level == null) ? "" : level.trim().toUpperCase();
        List<String> allEntries = new ArrayList<>();
        StringBuilder currentEntry = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Detecta inicio de nueva entrada de log (asume formato: fecha hora [nivel]
                // ...)
                if (line.matches("^\\d{4}-\\d{2}-\\d{2}.*(INFO|ERROR|WARN|DEBUG|TRACE).*")) {
                    if (currentEntry.length() > 0) {
                        allEntries.add(currentEntry.toString());
                        currentEntry.setLength(0);
                    }
                    currentEntry.append(line);
                } else {
                    if (currentEntry.length() > 0) {
                        currentEntry.append(System.lineSeparator()).append(line);
                    }
                }
            }
            if (currentEntry.length() > 0) {
                allEntries.add(currentEntry.toString());
            }
        } catch (IOException e) {
            logger.error("Error leyendo el archivo de logs", e);
        }
        // Filtra por nivel si se indica
        for (String entry : allEntries) {
            if (levelUpper.isEmpty() || entry.contains(levelUpper)) {
                // Solo la primera línea (mensaje principal)
                String firstLine = entry.split("\r?\n", 2)[0];
                result.add(firstLine);
            }
        }
        // Devuelve solo los últimos 'limit' bloques
        if (result.size() > limit) {
            return result.subList(result.size() - limit, result.size());
        }
        return result;
    }
}