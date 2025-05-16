package bot.facade;

public class BotPresenceDTO {
    private String statusText;
    private String activityType;
    private String activityName;
    private String streamUrl;

    public BotPresenceDTO() {}

    public BotPresenceDTO(String statusText, String activityType, String activityName, String streamUrl) {
        this.statusText = statusText;
        this.activityType = activityType;
        this.activityName = activityName;
        this.streamUrl = streamUrl;
    }

    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    public String getStreamUrl() { return streamUrl; }
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }
}
