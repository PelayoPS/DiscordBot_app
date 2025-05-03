package bot.services;

import bot.models.Experiencia;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la experiencia de los usuarios.
 * Permite consultar, aumentar y subir de nivel la experiencia.
 * 
 * @author PelayoPS
 */
public interface ExperienciaService extends Service<Experiencia, Long> {
    /**
     * Busca la experiencia por el ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Optional con la experiencia encontrada o vacío si no existe
     */
    Optional<Experiencia> findByIdUsuario(Long idUsuario);

    /**
     * Busca todas las experiencias con nivel mayor que el indicado.
     * 
     * @param nivel Nivel mínimo (exclusivo)
     * @return Lista de experiencias encontradas
     */
    List<Experiencia> findByNivelGreaterThan(int nivel);

    /**
     * Aumenta los puntos de experiencia de un usuario y sube de nivel si
     * corresponde.
     * 
     * @param idUsuario ID del usuario
     * @param puntos    Puntos a añadir
     */
    void aumentarExperiencia(Long idUsuario, int puntos);

    /**
     * Sube de nivel a un usuario.
     * 
     * @param idUsuario ID del usuario
     */
    void subirNivel(Long idUsuario);
}