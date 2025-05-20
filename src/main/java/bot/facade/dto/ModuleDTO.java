package bot.facade.dto;

import java.util.List;

/**
 * DTO para representar un módulo y sus comandos asociados.
 * 
 * @author PelayoPS
 */
public class ModuleDTO {
    private String nombre;
    private String descripcion;
    private boolean activo;
    private List<CommandDTO> comandos;

    /**
     * Constructor por defecto.
     */
    public ModuleDTO() {
    }

    /**
     * Constructor con todos los campos.
     *
     * @param nombre      Nombre del módulo
     * @param descripcion Descripción del módulo
     * @param activo      Estado de activación del módulo
     * @param comandos    Lista de comandos asociados al módulo
     */
    public ModuleDTO(String nombre, String descripcion, boolean activo, List<CommandDTO> comandos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.comandos = comandos;
    }

    /**
     * Obtiene el nombre del módulo.
     *
     * @return Nombre del módulo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del módulo.
     *
     * @param nombre Nombre del módulo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del módulo.
     *
     * @return Descripción del módulo
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del módulo.
     *
     * @param descripcion Descripción del módulo
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Indica si el módulo está activo.
     *
     * @return true si está activo, false en caso contrario
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado de activación del módulo.
     *
     * @param activo true para activar, false para desactivar
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Obtiene la lista de comandos asociados al módulo.
     *
     * @return Lista de comandos
     */
    public List<CommandDTO> getComandos() {
        return comandos;
    }

    /**
     * Establece la lista de comandos asociados al módulo.
     *
     * @param comandos Lista de comandos
     */
    public void setComandos(List<CommandDTO> comandos) {
        this.comandos = comandos;
    }
}