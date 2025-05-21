package bot.models;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Clase que representa un usuario en el sistema.
 * Incluye información sobre el tipo de usuario, experiencia y penalizaciones.
 * 
 * @author PelayoPS
 */
public class Usuario {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long idUsuario;
    private String tipoUsuario;
    private int nivel;
    private int puntosXp;
    private long timestampUltimoMensaje;
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
        this.nivel = 1;
        this.puntosXp = 0;
        this.timestampUltimoMensaje = 0L;
        this.penalizaciones = new ArrayList<>();
    }

    /**
     * Constructor de Usuario con experiencia.
     * 
     * @param idUsuario   ID del usuario
     * @param tipoUsuario Tipo de usuario
     * @param experiencia Experiencia asociada
     */
    // El constructor con Experiencia se elimina, ya no es necesario

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

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getPuntosXp() {
        return puntosXp;
    }

    public void setPuntosXp(int puntosXp) {
        this.puntosXp = puntosXp;
    }

    public long getTimestampUltimoMensaje() {
        return timestampUltimoMensaje;
    }

    public void setTimestampUltimoMensaje(long timestampUltimoMensaje) {
        this.timestampUltimoMensaje = timestampUltimoMensaje;
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