package bot.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;

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
     * Registra un mensaje de advertencia.
     * 
     * @param message El mensaje de advertencia.
     */
    public void logWarn(String message) {
        logger.warn(message);
    }

    /**
     * Registra un mensaje de debug.
     * 
     * @param message El mensaje de advertencia.
     */
    public void logDebug(String message) {
        logger.debug(message);
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
}