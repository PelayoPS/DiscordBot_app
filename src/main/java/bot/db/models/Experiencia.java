package bot.db.models;

/**
 * Clase que representa la experiencia de un usuario en el sistema.
 */
public class Experiencia {
    private Long idExperiencia;
    private Long idUsuario;
    private int nivel;
    private int puntosXp;
    private Usuario usuario;

    public Experiencia(Long idExperiencia, Long idUsuario, int nivel, int puntosXp) {
        this.idExperiencia = idExperiencia;
        this.idUsuario = idUsuario;
        this.nivel = nivel;
        this.puntosXp = puntosXp;
    }

    public Long getIdExperiencia() {
        return idExperiencia;
    }

    public Long getIdUsuario() {
        return idUsuario;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario.getExperiencia() != this) {
            usuario.setExperiencia(this);
        }
    }
}