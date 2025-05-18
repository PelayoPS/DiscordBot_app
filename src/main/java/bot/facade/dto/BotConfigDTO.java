package bot.facade.dto;

/**
 * DTO para exponer y modificar la configuración general del bot vía API REST.
 * No expone el token completo por seguridad, solo si está configurado.
 */
public class BotConfigDTO {
    private boolean tokenSet;

    public BotConfigDTO() {}

    public boolean isTokenSet() {
        return tokenSet;
    }
    public void setTokenSet(boolean tokenSet) {
        this.tokenSet = tokenSet;
    }
}
