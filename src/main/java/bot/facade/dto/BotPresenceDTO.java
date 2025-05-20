package bot.facade.dto;

/**
 * DTO para representar la presencia del bot (estado y actividad) en Discord.
 * 
 * @author PelayoPS
 */
public class BotPresenceDTO {
    private String statusText;
    private String activityType;
    private String activityName;
    private String streamUrl;

    /**
     * Constructor por defecto.
     */
    public BotPresenceDTO() {}

    /**
     * Constructor con todos los campos.
     *
     * @param statusText   Texto de estado del bot
     * @param activityType Tipo de actividad (PLAYING, STREAMING, etc.)
     * @param activityName Nombre de la actividad
     * @param streamUrl    URL de streaming (si aplica)
     */
    public BotPresenceDTO(String statusText, String activityType, String activityName, String streamUrl) {
        this.statusText = statusText;
        this.activityType = activityType;
        this.activityName = activityName;
        this.streamUrl = streamUrl;
    }

    /**
     * Obtiene el texto de estado del bot.
     *
     * @return Texto de estado
     */
    public String getStatusText() { return statusText; }

    /**
     * Establece el texto de estado del bot.
     *
     * @param statusText Texto de estado
     */
    public void setStatusText(String statusText) { this.statusText = statusText; }

    /**
     * Obtiene el tipo de actividad.
     *
     * @return Tipo de actividad
     */
    public String getActivityType() { return activityType; }

    /**
     * Establece el tipo de actividad.
     *
     * @param activityType Tipo de actividad
     */
    public void setActivityType(String activityType) { this.activityType = activityType; }

    /**
     * Obtiene el nombre de la actividad.
     *
     * @return Nombre de la actividad
     */
    public String getActivityName() { return activityName; }

    /**
     * Establece el nombre de la actividad.
     *
     * @param activityName Nombre de la actividad
     */
    public void setActivityName(String activityName) { this.activityName = activityName; }

    /**
     * Obtiene la URL de streaming.
     *
     * @return URL de streaming
     */
    public String getStreamUrl() { return streamUrl; }

    /**
     * Establece la URL de streaming.
     *
     * @param streamUrl URL de streaming
     */
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }
}
