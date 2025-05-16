package bot.facade;

public class BotTokenDTO {
    private String token;

    public BotTokenDTO() {}
    public BotTokenDTO(String token) { this.token = token; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
