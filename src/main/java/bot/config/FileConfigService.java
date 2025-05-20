package bot.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Implementación de ConfigService que carga la configuración desde un archivo
 * de propiedades.
 * 
 * @author PelayoPS
 */
public class FileConfigService implements ConfigService {
    private final Properties properties;
    private final String filePath;
    private static final bot.log.LoggingManager logger = new bot.log.LoggingManager();

    /**
     * Crea una instancia de FileConfigService y carga las propiedades desde el
     * archivo especificado.
     *
     * @param filePath Ruta del archivo de configuración
     */
    public FileConfigService(String filePath) {
        this.filePath = filePath;
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el archivo de configuración: " + filePath, e);
        }
    }

    /**
     * Establece el valor de una clave de configuración en memoria.
     *
     * @param key Clave de la configuración
     * @param value Valor a establecer
     */
    @Override
    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Persiste los cambios en el archivo de configuración.
     */
    @Override
    public void save() {
        try (java.io.FileOutputStream out = new java.io.FileOutputStream(filePath)) {
            properties.store(out, "Configuración del bot actualizada");
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de configuración: " + filePath, e);
        }
    }

    /**
     * Obtiene el valor de una clave de configuración.
     *
     * @param key Clave de la configuración
     * @return Valor asociado a la clave, o null si no existe
     */
    @Override
    public String get(String key) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            logger.logWarn("No se pudo recargar el archivo de configuración: " + filePath);
        }
        String value = properties.getProperty(key);
        if ("token".equals(key)) {
            logger.logInfo("[DEBUG] get('token') desde '" + filePath + "' => ''");
        }
        return value;
    }

    /**
     * Obtiene el valor de una clave de configuración, o un valor por defecto si no existe.
     *
     * @param key Clave de la configuración
     * @param defaultValue Valor por defecto si la clave no existe
     * @return Valor asociado a la clave, o defaultValue si no existe
     */
    @Override
    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
