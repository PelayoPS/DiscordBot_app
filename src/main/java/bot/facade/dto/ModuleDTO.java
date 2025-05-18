package bot.facade.dto;

import java.util.List;

public class ModuleDTO {
    private String nombre;
    private String descripcion;
    private boolean activo;
    private List<CommandDTO> comandos;

    public ModuleDTO() {
    }

    public ModuleDTO(String nombre, String descripcion, boolean activo, List<CommandDTO> comandos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.comandos = comandos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<CommandDTO> getComandos() {
        return comandos;
    }

    public void setComandos(List<CommandDTO> comandos) {
        this.comandos = comandos;
    }
}