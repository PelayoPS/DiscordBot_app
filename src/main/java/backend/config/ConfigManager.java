package backend.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import backend.log.LoggingManager;

/**
 * Clase para gestionar la configuración del bot.
 * Permite cargar, obtener y modificar propiedades de configuración.
 * 
 * @author PelayoPS
 */
public class ConfigManager {
    private static final LoggingManager logger = new LoggingManager();
    private static Properties properties = new Properties();
    private static final String DEFAULT_CONFIG_PATH = "src\\main\\resources\\config.properties";

    /**
     * Carga la configuración desde un archivo properties.
     * 
     * @param filePath Ruta del archivo de configuración
     * @return true si se cargó correctamente, false en caso contrario
     */
    public boolean loadConfig(String filePath) {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
            logger.logInfo("Configuración cargada desde: " + filePath);
            return true;
        } catch (IOException e) {
            logger.logError("Error al cargar el archivo de configuración: " + filePath, e);
            return false;
        }
    }

    /**
     * Carga la configuración desde el archivo por defecto (config.properties).
     * 
     * @return true si se cargó correctamente, false en caso contrario
     */
    public boolean loadConfig() {
        return loadConfig(DEFAULT_CONFIG_PATH);
    }

    /**
     * Obtiene una propiedad específica.
     * 
     * @param key Clave de la propiedad
     * @return Valor de la propiedad o null si no existe
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Obtiene una propiedad específica con un valor por defecto.
     * 
     * @param key          Clave de la propiedad
     * @param defaultValue Valor por defecto si la propiedad no existe
     * @return Valor de la propiedad o defaultValue si no existe
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Establece una propiedad.
     * 
     * @param key   Clave de la propiedad
     * @param value Valor de la propiedad
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveConfig();
    }

    /**
     * Guarda la configuración actual en el archivo por defecto (config.properties).
     */
    public void saveConfig() {
        try (java.io.FileOutputStream out = new java.io.FileOutputStream(DEFAULT_CONFIG_PATH)) {
            properties.store(out, "Configuración del bot actualizada");
            logger.logInfo("Configuración guardada en: " + DEFAULT_CONFIG_PATH);
        } catch (IOException e) {
            logger.logError("Error al guardar el archivo de configuración: " + DEFAULT_CONFIG_PATH, e);
        }
    }

}