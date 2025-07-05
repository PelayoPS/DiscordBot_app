package backend.facade.dto;

/**
 * DTO para representar un comando y su estado de activación.
 * 
 * @author PelayoPS
 */
public class CommandDTO {
    private String nombre;
    private boolean activo;

    /**
     * Constructor por defecto.
     */
    public CommandDTO() {
    }

    /**
     * Constructor con todos los campos.
     *
     * @param nombre Nombre del comando
     * @param activo Estado de activación del comando
     */
    public CommandDTO(String nombre, boolean activo) {
        this.nombre = nombre;
        this.activo = activo;
    }

    /**
     * Obtiene el nombre del comando.
     *
     * @return Nombre del comando
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del comando.
     *
     * @param nombre Nombre del comando
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Indica si el comando está activo.
     *
     * @return true si está activo, false en caso contrario
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado de activación del comando.
     *
     * @param activo true para activar, false para desactivar
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}