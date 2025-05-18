package bot.facade.dto;

/**
 * DTO para representar el estado y métricas del bot.
 * Incluye información como estado, tiempo activo, versión, uso de RAM y CPU.
 * 
 * @author PelayoPS
 */
public class BotStatusDTO {
    private String estado;
    private String tiempoActivo;
    private String version;
    private String ram;
    private String cpu;

    /**
     * Constructor vacío.
     */
    public BotStatusDTO() {
    }

    /**
     * Constructor con todos los campos.
     * 
     * @param estado       Estado del bot
     * @param tiempoActivo Tiempo activo del bot
     * @param version      Versión del bot
     * @param ram          Uso de RAM
     * @param cpu          Uso de CPU
     */
    public BotStatusDTO(String estado, String tiempoActivo, String version, String ram, String cpu) {
        this.estado = estado;
        this.tiempoActivo = tiempoActivo;
        this.version = version;
        this.ram = ram;
        this.cpu = cpu;
    }

    /**
     * Obtiene el estado del bot.
     * 
     * @return Estado del bot
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado del bot.
     * 
     * @param estado Estado del bot
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el tiempo activo del bot.
     * 
     * @return Tiempo activo
     */
    public String getTiempoActivo() {
        return tiempoActivo;
    }

    /**
     * Establece el tiempo activo del bot.
     * 
     * @param tiempoActivo Tiempo activo
     */
    public void setTiempoActivo(String tiempoActivo) {
        this.tiempoActivo = tiempoActivo;
    }

    /**
     * Obtiene la versión del bot.
     * 
     * @return Versión
     */
    public String getVersion() {
        return version;
    }

    /**
     * Establece la versión del bot.
     * 
     * @param version Versión
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Obtiene el uso de RAM.
     * 
     * @return RAM usada
     */
    public String getRam() {
        return ram;
    }

    /**
     * Establece el uso de RAM.
     * 
     * @param ram RAM usada
     */
    public void setRam(String ram) {
        this.ram = ram;
    }

    /**
     * Obtiene el uso de CPU.
     * 
     * @return CPU usada
     */
    public String getCpu() {
        return cpu;
    }

    /**
     * Establece el uso de CPU.
     * 
     * @param cpu CPU usada
     */
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }
}
