package backend.facade.dto;

/**
 * DTO para representar la clave de Gemini.
 * 
 * @author PelayoPS
 */
public class GeminiKeyDTO {
    private String key;

    /**
     * Constructor por defecto.
     */
    public GeminiKeyDTO() {}

    /**
     * Constructor con clave.
     *
     * @param key Clave de Gemini
     */
    public GeminiKeyDTO(String key) { this.key = key; }

    /**
     * Obtiene la clave de Gemini.
     *
     * @return Clave de Gemini
     */
    public String getKey() { return key; }

    /**
     * Establece la clave de Gemini.
     *
     * @param key Clave de Gemini
     */
    public void setKey(String key) { this.key = key; }
}
