package backend.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Implementación de ConfigService que carga la configuración desde un archivo
 * de propiedades usando el classpath para compatibilidad con JAR empaquetado.
 * 
 * @author PelayoPS
 */
public class FileConfigService implements ConfigService {
    private final Properties properties;
    private final Path configFile;
    private static final backend.log.LoggingManager logger = new backend.log.LoggingManager();    /**
     * Crea una instancia de FileConfigService y carga las propiedades desde el
     * classpath o archivo del sistema.
     *
     * @param resourcePath Ruta del recurso de configuración (ej: "config.properties")
     */
    public FileConfigService(String resourcePath) {
        this.properties = new Properties();
        
        // Convertir ruta de desarrollo a nombre de recurso
        String resourceName = resourcePath.replace("src/main/resources/", "")
                                         .replace("src\\main\\resources\\", "");
        
        Path tempConfigFile = null;
        
        // Intentar cargar desde classpath primero (JAR empaquetado)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is != null) {
                properties.load(is);
                logger.logInfo("Configuración cargada desde classpath: " + resourceName);
                // Para guardar cambios, crear archivo en directorio de trabajo
                tempConfigFile = Paths.get(System.getProperty("user.dir"), resourceName);
            } else {
                throw new IOException("Recurso no encontrado en classpath: " + resourceName);
            }
        } catch (IOException e) {
            // Si falla classpath, intentar cargar como archivo del sistema (desarrollo)
            try (FileInputStream fis = new FileInputStream(resourcePath)) {
                properties.load(fis);
                logger.logInfo("Configuración cargada desde archivo: " + resourcePath);
                tempConfigFile = Paths.get(resourcePath);
            } catch (IOException ex) {
                throw new RuntimeException("No se pudo cargar el archivo de configuración: " + resourcePath, ex);
            }
        }
        
        this.configFile = tempConfigFile;
    }/**
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
        try {
            // Crear directorio padre si no existe
            Files.createDirectories(configFile.getParent());
            
            try (FileOutputStream out = new FileOutputStream(configFile.toFile())) {
                properties.store(out, "Configuración del bot actualizada");
                logger.logInfo("Configuración guardada en: " + configFile.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de configuración: " + configFile.toString(), e);
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
        // Recargar desde archivo si existe (para cambios externos)
        if (Files.exists(configFile)) {
            try (FileInputStream fis = new FileInputStream(configFile.toFile())) {
                properties.load(fis);
            } catch (IOException e) {
                logger.logWarn("No se pudo recargar el archivo de configuración: " + configFile.toString());
            }
        }
        
        String value = properties.getProperty(key);
        if ("token".equals(key)) {
            logger.logInfo("[DEBUG] get('token') desde '" + configFile.toString() + "' => ''");
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
