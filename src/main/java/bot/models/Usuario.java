package bot.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un usuario en el sistema.
 * Incluye información sobre el tipo de usuario, experiencia y penalizaciones.
 * 
 * @author PelayoPS
 */
public class Usuario {
    private Long idUsuario;
    private String tipoUsuario;
    private Experiencia experiencia;
    private List<Penalizacion> penalizaciones;

    /**
     * Constructor de Usuario.
     * 
     * @param idUsuario   ID del usuario
     * @param tipoUsuario Tipo de usuario
     */
    public Usuario(Long idUsuario, String tipoUsuario) {
        this.idUsuario = idUsuario;
        this.tipoUsuario = tipoUsuario;
        this.penalizaciones = new ArrayList<>();
    }

    /**
     * Constructor de Usuario con experiencia.
     * 
     * @param idUsuario   ID del usuario
     * @param tipoUsuario Tipo de usuario
     * @param experiencia Experiencia asociada
     */
    public Usuario(Long idUsuario, String tipoUsuario, Experiencia experiencia) {
        this.idUsuario = idUsuario;
        this.tipoUsuario = tipoUsuario;
        this.experiencia = experiencia;
        this.experiencia.setUsuario(this);
        this.penalizaciones = new ArrayList<>();
    }

    /**
     * Obtiene el ID del usuario.
     * 
     * @return ID del usuario
     */
    public Long getIdUsuario() {
        return idUsuario;
    }

    /**
     * Obtiene el tipo de usuario.
     * 
     * @return Tipo de usuario
     */
    public String getTipoUsuario() {
        return tipoUsuario;
    }

    /**
     * Establece el tipo de usuario.
     * 
     * @param tipoUsuario Tipo de usuario
     */
    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    /**
     * Obtiene la experiencia asociada al usuario.
     * 
     * @return Experiencia
     */
    public Experiencia getExperiencia() {
        return experiencia;
    }

    /**
     * Establece la experiencia asociada al usuario.
     * 
     * @param experiencia Experiencia a asociar
     */
    public void setExperiencia(Experiencia experiencia) {
        this.experiencia = experiencia;
    }

    /**
     * Obtiene la lista de penalizaciones del usuario.
     * 
     * @return Lista de penalizaciones
     */
    public List<Penalizacion> getPenalizaciones() {
        return penalizaciones;
    }

    /**
     * Añade una penalización al usuario.
     * 
     * @param penalizacion Penalización a añadir
     */
    public void addPenalizacion(Penalizacion penalizacion) {
        this.penalizaciones.add(penalizacion);
    }
}