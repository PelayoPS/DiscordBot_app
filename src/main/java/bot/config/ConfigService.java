package bot.config;

/**
 * Interfaz para el servicio de configuración del bot.
 * Permite obtener valores de configuración desde diferentes fuentes.
 * 
 * @author PelayoPS
 */
public interface ConfigService {
    /**
     * Obtiene el valor de una clave de configuración.
     * 
     * @param key Clave de la configuración
     * @return Valor asociado a la clave, o null si no existe
     */
    String get(String key);

    /**
     * Obtiene el valor de una clave de configuración, o un valor por defecto si no
     * existe.
     * 
     * @param key          Clave de la configuración
     * @param defaultValue Valor por defecto si la clave no existe
     * @return Valor asociado a la clave, o defaultValue si no existe
     */
    String get(String key, String defaultValue);
    // Métodos adicionales según necesidad
}
