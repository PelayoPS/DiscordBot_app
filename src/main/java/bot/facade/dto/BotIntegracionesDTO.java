package bot.facade.dto;

/**
 * DTO para representar el estado de las integraciones del bot (JDA, IA, Base de
 * datos).
 * 
 * @author PelayoPS
 */
public class BotIntegracionesDTO {
    /**
     * Estado de la integración con JDA (Discord).
     */
    public static class JdaStatus {
        public boolean conectado;
        public long ping;

        /**
         * Constructor de JdaStatus.
         * 
         * @param conectado Si está conectado a Discord
         * @param ping      Ping actual de la conexión
         */
        public JdaStatus(boolean conectado, long ping) {
            this.conectado = conectado;
            this.ping = ping;
        }
    }

    /**
     * Estado de la integración con la API de IA.
     */
    public static class AiApiStatus {
        public boolean disponible;
        public String mensaje;

        /**
         * Constructor de AiApiStatus.
         * 
         * @param disponible Si la API está disponible
         * @param mensaje    Mensaje de estado de la API
         */
        public AiApiStatus(boolean disponible, String mensaje) {
            this.disponible = disponible;
            this.mensaje = mensaje;
        }
    }

    /**
     * Estado de la integración con la base de datos.
     */
    public static class DatabaseStatus {
        public boolean conectada;
        public long ping;

        /**
         * Constructor de DatabaseStatus.
         * 
         * @param conectada Si la base de datos está conectada
         * @param ping      Ping de la base de datos
         */
        public DatabaseStatus(boolean conectada, long ping) {
            this.conectada = conectada;
            this.ping = ping;
        }
    }

    public JdaStatus jda;
    public AiApiStatus aiApi;
    public DatabaseStatus database;

    /**
     * Constructor de BotIntegracionesDTO.
     * 
     * @param jda      Estado de JDA
     * @param aiApi    Estado de la API de IA
     * @param database Estado de la base de datos
     */
    public BotIntegracionesDTO(JdaStatus jda, AiApiStatus aiApi, DatabaseStatus database) {
        this.jda = jda;
        this.aiApi = aiApi;
        this.database = database;
    }
}