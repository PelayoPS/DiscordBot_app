package backend.facade.dto;

/**
 * DTO para representar el token del bot.
 * 
 * @author PelayoPS
 */
public class BotTokenDTO {
    private String token;

    /**
     * Constructor por defecto.
     */
    public BotTokenDTO() {}

    /**
     * Constructor con token.
     *
     * @param token Token del bot
     */
    public BotTokenDTO(String token) { this.token = token; }

    /**
     * Obtiene el token del bot.
     *
     * @return Token del bot
     */
    public String getToken() { return token; }

    /**
     * Establece el token del bot.
     *
     * @param token Token del bot
     */
    public void setToken(String token) { this.token = token; }
}
