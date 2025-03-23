package bot.db.models;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Clase que representa una penalización aplicada a un usuario.
 */
public class Penalizacion {
    private Long idPenalizacion;
    private Long idUsuario;
    private Long idAdminMod;
    private String tipo;
    private LocalDateTime fecha;
    private String razon;
    private Duration duracion;
    private Usuario usuario;
    private Usuario adminMod;

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

    public Long getIdPenalizacion() {
        return idPenalizacion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdAdminMod() {
        return idAdminMod;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getRazon() {
        return razon;
    }

    public Duration getDuracion() {
        return duracion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (!usuario.getPenalizaciones().contains(this)) {
            usuario.addPenalizacion(this);
        }
    }

    public Usuario getAdminMod() {
        return adminMod;
    }

    public void setAdminMod(Usuario adminMod) {
        this.adminMod = adminMod;
    }
}