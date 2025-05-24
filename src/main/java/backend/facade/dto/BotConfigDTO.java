package backend.facade.dto;

/**
 * DTO para exponer y modificar la configuración general del bot vía API REST.
 * No expone el token completo por seguridad, solo si está configurado.
 * 
 * @author PelayoPS
 */
public class BotConfigDTO {
    private boolean tokenSet;

    /**
     * Constructor por defecto.
     */
    public BotConfigDTO() {}

    /**
     * Indica si el token está configurado.
     *
     * @return true si el token está configurado, false en caso contrario
     */
    public boolean isTokenSet() {
        return tokenSet;
    }

    /**
     * Establece si el token está configurado.
     *
     * @param tokenSet true si el token está configurado, false en caso contrario
     */
    public void setTokenSet(boolean tokenSet) {
        this.tokenSet = tokenSet;
    }
}
