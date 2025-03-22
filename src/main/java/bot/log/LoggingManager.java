package bot.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase para manejar el logging del sistema.
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
}