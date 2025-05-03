package bot.models;

/**
 * Clase que representa la experiencia de un usuario en el sistema.
 * Incluye nivel, puntos de experiencia y relación con el usuario.
 * 
 * @author PelayoPS
 */
public class Experiencia {
    private Long idExperiencia;
    private Long idUsuario;
    private int nivel;
    private int puntosXp;
    private Usuario usuario;

    /**
     * Constructor de Experiencia.
     * 
     * @param idExperiencia ID de la experiencia
     * @param idUsuario     ID del usuario
     * @param nivel         Nivel del usuario
     * @param puntosXp      Puntos de experiencia
     */
    public Experiencia(Long idExperiencia, Long idUsuario, int nivel, int puntosXp) {
        this.idExperiencia = idExperiencia;
        this.idUsuario = idUsuario;
        this.nivel = nivel;
        this.puntosXp = puntosXp;
    }

    /**
     * Obtiene el ID de la experiencia.
     * 
     * @return ID de la experiencia
     */
    public Long getIdExperiencia() {
        return idExperiencia;
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
     * Obtiene el nivel del usuario.
     * 
     * @return Nivel
     */
    public int getNivel() {
        return nivel;
    }

    /**
     * Establece el nivel del usuario.
     * 
     * @param nivel Nivel a establecer
     */
    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    /**
     * Obtiene los puntos de experiencia.
     * 
     * @return Puntos de experiencia
     */
    public int getPuntosXp() {
        return puntosXp;
    }

    /**
     * Establece los puntos de experiencia.
     * 
     * @param puntosXp Puntos de experiencia a establecer
     */
    public void setPuntosXp(int puntosXp) {
        this.puntosXp = puntosXp;
    }

    /**
     * Obtiene el usuario asociado.
     * 
     * @return Usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario asociado.
     * 
     * @param usuario Usuario a asociar
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario.getExperiencia() != this) {
            usuario.setExperiencia(this);
        }
    }
}