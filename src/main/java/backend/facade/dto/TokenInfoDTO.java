package backend.facade.dto;

/**
 * DTO para informar si el token del bot está configurado.
 * 
 * @author PelayoPS
 */
public class TokenInfoDTO {
    private boolean configured;

    /**
     * Constructor con parámetro de configuración.
     *
     * @param configured true si el token está configurado, false en caso contrario
     */
    public TokenInfoDTO(boolean configured) {
        this.configured = configured;
    }

    /**
     * Indica si el token está configurado.
     *
     * @return true si está configurado, false en caso contrario
     */
    public boolean isConfigured() {
        return configured;
    }

    /**
     * Establece si el token está configurado.
     *
     * @param configured true si está configurado, false en caso contrario
     */
    public void setConfigured(boolean configured) {
        this.configured = configured;
    }
}
