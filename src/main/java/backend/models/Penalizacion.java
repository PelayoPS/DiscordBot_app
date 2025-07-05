package backend.models;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Clase que representa una penalización aplicada a un usuario.
 * Incluye información sobre el tipo, fecha, razón, duración y relación con
 * usuarios.
 * 
 * @author PelayoPS
 */
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class Penalizacion {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long idPenalizacion;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long idUsuario;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long idAdminMod;
    private String tipo;
    private LocalDateTime fecha;
    private String razon;
    private Duration duracion;
    private Usuario usuario;
    private Usuario adminMod;

    /**
     * Constructor de Penalizacion.
     * 
     * @param idPenalizacion ID de la penalización
     * @param idUsuario      ID del usuario penalizado
     * @param idAdminMod     ID del admin/mod que aplica la penalización
     * @param tipo           Tipo de penalización
     * @param fecha          Fecha de la penalización
     * @param razon          Razón de la penalización
     * @param duracion       Duración de la penalización
     */
    public Penalizacion(Long idPenalizacion, Long idUsuario, Long idAdminMod, String tipo,
            LocalDateTime fecha, String razon, Duration duracion) {
        this.idPenalizacion = idPenalizacion;
        this.idUsuario = idUsuario;
        this.idAdminMod = idAdminMod;
        this.tipo = tipo;
        this.fecha = fecha;
        this.razon = razon;
        this.duracion = duracion;
    }

    /**
     * Obtiene el ID de la penalización.
     * 
     * @return ID de la penalización
     */
    public Long getIdPenalizacion() {
        return idPenalizacion;
    }

    /**
     * Obtiene el ID del usuario penalizado.
     * 
     * @return ID del usuario
     */
    public Long getIdUsuario() {
        return idUsuario;
    }

    /**
     * Obtiene el ID del admin/mod que aplicó la penalización.
     * 
     * @return ID del admin/mod
     */
    public Long getIdAdminMod() {
        return idAdminMod;
    }

    /**
     * Obtiene el tipo de penalización.
     * 
     * @return Tipo de penalización
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Obtiene la fecha de la penalización.
     * 
     * @return Fecha de la penalización
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Obtiene la razón de la penalización.
     * 
     * @return Razón de la penalización
     */
    public String getRazon() {
        return razon;
    }

    /**
     * Obtiene la duración de la penalización.
     * 
     * @return Duración de la penalización
     */
    public Duration getDuracion() {
        return duracion;
    }

    /**
     * Obtiene el usuario penalizado.
     * 
     * @return Usuario penalizado
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario penalizado.
     * 
     * @param usuario Usuario penalizado
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (!usuario.getPenalizaciones().contains(this)) {
            usuario.addPenalizacion(this);
        }
    }

    /**
     * Obtiene el admin/mod que aplicó la penalización.
     * 
     * @return Usuario admin/mod
     */
    public Usuario getAdminMod() {
        return adminMod;
    }

    /**
     * Establece el admin/mod que aplicó la penalización.
     * 
     * @param adminMod Usuario admin/mod
     */
    public void setAdminMod(Usuario adminMod) {
        this.adminMod = adminMod;
    }
}