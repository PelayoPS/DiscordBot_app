package bot.db.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un usuario en el sistema.
 */
public class Usuario {
    private Long idUsuario;
    private String tipoUsuario;
    private Experiencia experiencia;
    private List<Penalizacion> penalizaciones;

    public Usuario(Long idUsuario, String tipoUsuario) {
        this.idUsuario = idUsuario;
        this.tipoUsuario = tipoUsuario;
        this.penalizaciones = new ArrayList<>();
    }

    public Usuario(Long idUsuario, String tipoUsuario, Experiencia experiencia) {
        this.idUsuario = idUsuario;
        this.tipoUsuario = tipoUsuario;
        this.experiencia = experiencia;
        this.experiencia.setUsuario(this);
        this.penalizaciones = new ArrayList<>();
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Experiencia getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(Experiencia experiencia) {
        this.experiencia = experiencia;
    }

    public List<Penalizacion> getPenalizaciones() {
        return penalizaciones;
    }

    public void addPenalizacion(Penalizacion penalizacion) {
        this.penalizaciones.add(penalizacion);
    }
}