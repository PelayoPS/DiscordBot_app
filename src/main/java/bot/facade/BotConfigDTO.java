package bot.facade;

/**
 * DTO para exponer y modificar la configuración general del bot vía API REST.
 * No expone el token completo por seguridad, solo si está configurado.
 */
public class BotConfigDTO {
    private boolean tokenSet;
    private String statusText;
    private String activityType;
    private String activityName;
    private String streamUrl;

    public BotConfigDTO() {}

    public boolean isTokenSet() {
        return tokenSet;
    }
    public void setTokenSet(boolean tokenSet) {
        this.tokenSet = tokenSet;
    }
    public String getStatusText() {
        return statusText;
    }
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    public String getActivityType() {
        return activityType;
    }
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    public String getActivityName() {
        return activityName;
    }
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public String getStreamUrl() {
        return streamUrl;
    }
    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
}
