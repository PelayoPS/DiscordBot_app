package bot.facade;

public class TokenInfoDTO {
    private boolean configured;

    public TokenInfoDTO(boolean configured) {
        this.configured = configured;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }
}
